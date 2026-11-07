
package com.recruitment.platform.interview.service;

import com.recruitment.platform.application.dto.UpdateApplicationStatusRequest;
import com.recruitment.platform.application.entity.Application;
import com.recruitment.platform.application.entity.enums.ApplicationStatus;
import com.recruitment.platform.application.repository.ApplicationRepository;
import com.recruitment.platform.application.service.ApplicationService;
import com.recruitment.platform.company.repository.CompanyRecruiterRepository;
import com.recruitment.platform.interview.dto.*;
import com.recruitment.platform.interview.entity.InterviewProcess;
import com.recruitment.platform.interview.entity.InterviewRound;
import com.recruitment.platform.interview.entity.enums.InterviewMode;
import com.recruitment.platform.interview.entity.enums.InterviewOutcome;
import com.recruitment.platform.interview.entity.enums.InterviewRoundStatus;
import com.recruitment.platform.interview.mapper.InterviewMapper;
import com.recruitment.platform.interview.repository.InterviewProcessRepository;
import com.recruitment.platform.interview.repository.InterviewRoundRepository;
import com.recruitment.platform.job.entity.Job;
import com.recruitment.platform.job.repository.JobRepository;
import com.recruitment.platform.user.entity.User;
import com.recruitment.platform.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

import static com.recruitment.platform.application.entity.enums.ApplicationStatus.SHORTLISTED;
import static com.recruitment.platform.user.entity.UserRole.CANDIDATE;
import static com.recruitment.platform.user.entity.UserRole.RECRUITER;

@Service
public class InterviewService {

    @Autowired
    private InterviewProcessRepository interviewProcessRepository;

    @Autowired
    private InterviewRoundRepository interviewRoundRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRecruiterRepository companyRecruiterRepository;

    @Autowired
    private InterviewMapper interviewMapper;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ApplicationService applicationService;


    @Transactional
    public RecruiterInterviewResponse createInterviewRound(
            Long applicationId,
            User recruiter,
            CreateInterviewRoundRequest request) {

        // 1. Validate recruiter role
        if (recruiter.getRole() != RECRUITER) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only recruiters can schedule interviews"
            );
        }

        // 2. Find application
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Application not found"
                ));

        // 3. Validate candidate associated with the application
        User candidate = application.getCandidate();

        if (candidate == null || candidate.getRole() != CANDIDATE) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Application is not associated with a valid candidate"
            );
        }

        // 4. Application must remain shortlisted during the interview process
        if (application.getStatus() != SHORTLISTED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Application must be shortlisted to schedule interviews"
            );
        }

        // 5. Validate that recruiter belongs to the company's recruiter list
        Long companyId = application.getJob().getCompany().getId();

        boolean isCompanyRecruiter =
                companyRecruiterRepository.existsByCompanyIdAndRecruiterId(
                        companyId,
                        recruiter.getId()
                );

        if (!isCompanyRecruiter) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not associated with this company"
            );
        }

        // 6. Find and validate interviewer
        User interviewer = userRepository.findById(request.getInterviewerId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Interviewer not found"
                ));

        if (interviewer.getRole() != RECRUITER) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Interviewer must have a recruiter role"
            );
        }

        boolean interviewerBelongsToCompany =
                companyRecruiterRepository.existsByCompanyIdAndRecruiterId(
                        companyId,
                        interviewer.getId()
                );

        if (!interviewerBelongsToCompany) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Interviewer must belong to the same company"
            );
        }

        // 7. Validate interview mode details
        if (request.getMode() == InterviewMode.ONLINE
                && (request.getMeetingLink() == null
                || request.getMeetingLink().isBlank())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Meeting link is required for online interviews"
            );
        }

        if (request.getMode() == InterviewMode.ONSITE
                && (request.getLocation() == null
                || request.getLocation().isBlank())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Location is required for onsite interviews"
            );
        }

        // 8. Find or create interview process
        InterviewProcess process;
        Integer roundNumber;

        var existingProcess =
                interviewProcessRepository.findByApplicationId(applicationId);

        if (existingProcess.isEmpty()) {

            // First interview round
            if (request.getTotalRounds() == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Total rounds must be provided for the first interview round"
                );
            }

            process = new InterviewProcess();
            process.setApplication(application);
            process.setTotalRounds(request.getTotalRounds());

            process = interviewProcessRepository.save(process);

            roundNumber = 1;

        } else {

            // Subsequent interview round
            process = existingProcess.get();

            if (request.getTotalRounds() != null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Total rounds can only be specified for the first interview round"
                );
            }

            InterviewRound previousRound =
                    interviewRoundRepository
                            .findTopByInterviewProcessIdOrderByRoundNumberDesc(
                                    process.getId()
                            )
                            .orElseThrow(() -> new ResponseStatusException(
                                    HttpStatus.INTERNAL_SERVER_ERROR,
                                    "Interview process has no rounds"
                            ));

            if (previousRound.getStatus() != InterviewRoundStatus.COMPLETED
                    || previousRound.getOutcome() != InterviewOutcome.PASSED) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "The previous round must be completed and passed before scheduling the next round"
                );
            }

            roundNumber = previousRound.getRoundNumber() + 1;

            if (roundNumber > process.getTotalRounds()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Maximum number of interview rounds has been reached"
                );
            }
        }

        // 9. Create interview round
        InterviewRound round = interviewMapper.toEntity(request);

        round.setInterviewProcess(process);
        round.setRoundNumber(roundNumber);
        round.setInterviewer(interviewer);
        round.setCreatedBy(recruiter);
        round.setStatus(InterviewRoundStatus.SCHEDULED);

        // Keep only the relevant location/link for the selected mode
        if (request.getMode() == InterviewMode.ONLINE) {
            round.setLocation(null);
        } else {
            round.setMeetingLink(null);
        }

        InterviewRound savedRound =
                interviewRoundRepository.save(round);

        // 10. Return recruiter response
        return interviewMapper.toRecruiterResponse(savedRound);
    }


    public Page<?> getMyInterviews(User user, Pageable pageable) {

        if (user.getRole() == CANDIDATE) {

            return interviewRoundRepository
                    .findByInterviewProcess_Application_Candidate_Id(
                            user.getId(),
                            pageable
                    )
                    .map(interviewMapper::toCandidateResponse);
        }

        if (user.getRole() == RECRUITER) {

            return interviewRoundRepository
                    .findByInterviewerId(user.getId(), pageable)
                    .map(interviewMapper::toRecruiterResponse);
        }

        throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "You are not authorized to view interviews"
        );
    }

    public Page<RecruiterInterviewResponse> getJobInterviews(
            Long jobId,
            User recruiter,
            Pageable pageable) {

        if (recruiter.getRole() != RECRUITER) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only recruiters can view job interviews"
            );
        }

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Job not found"
                ));

        Long companyId = job.getCompany().getId();

        boolean isCompanyRecruiter =
                companyRecruiterRepository.existsByCompanyIdAndRecruiterId(
                        companyId,
                        recruiter.getId()
                );

        if (!isCompanyRecruiter) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not authorized to view interviews for this job"
            );
        }

        return interviewRoundRepository
                .findByInterviewProcess_Application_Job_Id(jobId, pageable)
                .map(interviewMapper::toRecruiterResponse);
    }


    public Object getInterviewById(Long interviewId, User user) {

        InterviewRound round = interviewRoundRepository.findById(interviewId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Interview not found"
                ));

        Application application = round.getInterviewProcess().getApplication();

        if (user.getRole() == CANDIDATE) {

            if (!(application.getCandidate().getId()==user.getId())) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "You are not authorized to view this interview"
                );
            }

            return interviewMapper.toCandidateResponse(round);
        }

        if (user.getRole() == RECRUITER) {

            Long companyId = application.getJob().getCompany().getId();

            boolean isCompanyRecruiter =
                    companyRecruiterRepository.existsByCompanyIdAndRecruiterId(
                            companyId,
                            user.getId()
                    );

            if (!isCompanyRecruiter) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "You are not authorized to view this interview"
                );
            }

            return interviewMapper.toRecruiterResponse(round);
        }

        throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "You are not authorized to view interviews"
        );
    }



    @Transactional
    public RecruiterInterviewResponse updateInterview(
            Long interviewId,
            User recruiter,
            UpdateInterviewRequest request) {

        if (recruiter.getRole() != RECRUITER) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only recruiters can update interviews"
            );
        }

        InterviewRound round = interviewRoundRepository.findById(interviewId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Interview not found"
                ));

        if (round.getStatus() == InterviewRoundStatus.COMPLETED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Completed interviews cannot be updated"
            );
        }

        Application application =
                round.getInterviewProcess().getApplication();

        Long companyId = application.getJob().getCompany().getId();

        boolean isCompanyRecruiter =
                companyRecruiterRepository.existsByCompanyIdAndRecruiterId(
                        companyId,
                        recruiter.getId()
                );

        if (!isCompanyRecruiter) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not authorized to update this interview"
            );
        }

        if (request.getRoundName() != null) {
            round.setRoundName(request.getRoundName());
        }

        if (request.getScheduledAt() != null) {
            round.setScheduledAt(request.getScheduledAt());
        }

        if (request.getDurationMinutes() != null) {
            round.setDurationMinutes(request.getDurationMinutes());
        }

        if (request.getMode() != null) {
            round.setMode(request.getMode());
        }

        if (request.getMeetingLink() != null) {
            round.setMeetingLink(request.getMeetingLink());
        }

        if (request.getLocation() != null) {
            round.setLocation(request.getLocation());
        }

        if (request.getInterviewerId() != null) {

            User interviewer = userRepository.findById(request.getInterviewerId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Interviewer not found"
                    ));

            if (interviewer.getRole() != RECRUITER) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Assigned interviewer must be a recruiter"
                );
            }

            boolean isInterviewerMember =
                    companyRecruiterRepository.existsByCompanyIdAndRecruiterId(
                            companyId,
                            interviewer.getId()
                    );

            if (!isInterviewerMember) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Interviewer must belong to the same company"
                );
            }

            round.setInterviewer(interviewer);
        }

        // Validate the final interview mode and its required field.
        if (round.getMode() == InterviewMode.ONLINE) {

            if (round.getMeetingLink() == null ||
                    round.getMeetingLink().isBlank()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Meeting link is required for online interviews"
                );
            }

            round.setLocation(null);

        } else if (round.getMode() == InterviewMode.ONSITE) {

            if (round.getLocation() == null ||
                    round.getLocation().isBlank()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Location is required for onsite interviews"
                );
            }

            round.setMeetingLink(null);
        }

        // Reactivate a cancelled interview after successful rescheduling.
        if (round.getStatus() == InterviewRoundStatus.CANCELLED) {
            round.setStatus(InterviewRoundStatus.SCHEDULED);
            round.setCancelledAt(null);
            round.setCancellationReason(null);
        }

        InterviewRound updatedRound =
                interviewRoundRepository.save(round);

        return interviewMapper.toRecruiterResponse(updatedRound);
    }


    @Transactional
    public RecruiterInterviewResponse cancelInterview(
            Long interviewId,
            User recruiter,
            CancelInterviewRequest request) {

        if (recruiter.getRole() != RECRUITER) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only recruiters can cancel interviews"
            );
        }

        InterviewRound round = interviewRoundRepository.findById(interviewId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Interview not found"
                ));

        if (round.getStatus() != InterviewRoundStatus.SCHEDULED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only scheduled interviews can be cancelled"
            );
        }

        Long companyId = round.getInterviewProcess()
                .getApplication()
                .getJob()
                .getCompany()
                .getId();

        boolean isCompanyRecruiter =
                companyRecruiterRepository.existsByCompanyIdAndRecruiterId(
                        companyId,
                        recruiter.getId()
                );

        if (!isCompanyRecruiter) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not authorized to cancel this interview"
            );
        }

        round.setStatus(InterviewRoundStatus.CANCELLED);
        round.setCancelledAt(LocalDateTime.now());

        if (request.getCancellationReason() != null) {
            round.setCancellationReason(
                    request.getCancellationReason()
            );
        }

        InterviewRound cancelledRound =
                interviewRoundRepository.save(round);

        return interviewMapper.toRecruiterResponse(cancelledRound);
    }


    @Transactional
    public RecruiterInterviewResponse submitInterviewFeedback(
            Long interviewId,
            User interviewer,
            SubmitInterviewFeedbackRequest request) {

        if (interviewer.getRole() != RECRUITER) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only recruiters can submit interview feedback"
            );
        }

        InterviewRound round = interviewRoundRepository.findById(interviewId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Interview not found"
                ));

        if (!(round.getInterviewer().getId()==(interviewer.getId()))) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only the assigned interviewer can submit feedback"
            );
        }

        if (round.getStatus() != InterviewRoundStatus.SCHEDULED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Feedback can only be submitted for scheduled interviews"
            );
        }

        round.setFeedback(request.getFeedback());
        round.setStrengths(request.getStrengths());
        round.setAreasForImprovement(request.getAreasForImprovement());
        round.setOutcome(request.getOutcome());
        round.setStatus(InterviewRoundStatus.COMPLETED);
        round.setFeedbackSubmittedAt(LocalDateTime.now());

        InterviewRound completedRound =
                interviewRoundRepository.save(round);

        if (request.getOutcome() == InterviewOutcome.FAILED) {

            Application application =
                    round.getInterviewProcess().getApplication();

            UpdateApplicationStatusRequest statusRequest =
                    new UpdateApplicationStatusRequest();

            statusRequest.setStatus(ApplicationStatus.REJECTED);

            applicationService.updateApplicationStatus(
                    application.getId(),
                    interviewer,
                    statusRequest
            );
        }

        return interviewMapper.toRecruiterResponse(completedRound);
    }

}
