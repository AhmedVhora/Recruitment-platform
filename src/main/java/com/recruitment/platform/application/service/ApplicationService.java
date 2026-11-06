package com.recruitment.platform.application.service;

import com.recruitment.platform.application.dto.ApplicationResponse;
import com.recruitment.platform.application.dto.ApplyJobRequest;
import com.recruitment.platform.application.dto.UpdateApplicationStatusRequest;
import com.recruitment.platform.application.entity.Application;
import com.recruitment.platform.application.entity.enums.ApplicationStatus;
import com.recruitment.platform.application.mapper.ApplicationMapper;
import com.recruitment.platform.application.repository.ApplicationRepository;
import com.recruitment.platform.company.repository.CompanyRecruiterRepository;
import com.recruitment.platform.job.entity.Job;
import com.recruitment.platform.job.entity.enums.JobStatus;
import com.recruitment.platform.job.repository.JobRepository;
import com.recruitment.platform.job.service.JobService;
import com.recruitment.platform.profile.dto.CandidateProfileResponse;
import com.recruitment.platform.profile.service.CandidateProfileService;
import com.recruitment.platform.user.entity.User;
import com.recruitment.platform.user.entity.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@Service
public class ApplicationService {

    @Autowired
    private JobService jobService;

    @Autowired
    private CompanyRecruiterRepository companyRecruiterRepository;

    @Autowired
    private CandidateProfileService candidateProfileService;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ApplicationMapper applicationMapper;

    @Transactional
    public ApplicationResponse applyToJob(
            Long jobId, User candidate, ApplyJobRequest request) {

        // 1. Verify candidate role
        if (candidate.getRole() != UserRole.CANDIDATE) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only candidates can apply for jobs."
            );
        }

        // 2. Find job
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Job does not exist."
                ));

        // 3. Verify job is published
        if (job.getStatus() != JobStatus.PUBLISHED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "This job is not accepting applications."
            );
        }

        // 4. Verify remaining vacancies
        if (job.getRemainingVacancies() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No vacancies are currently available."
            );
        }

        // 5. Verify application deadline
        if (job.getApplicationDeadline() == null
                || job.getApplicationDeadline().isBefore(LocalDate.now())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "The application deadline has passed."
            );
        }

        // 6. Prevent duplicate applications
        if (applicationRepository.existsByCandidate_IdAndJob_Id(
                candidate.getId(), job.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "You have already applied for this job."
            );
        }

        // 7. Verify resume ownership
        CandidateProfileResponse profile =
                candidateProfileService.getProfile(candidate);

        if (profile.getResumeUrls() == null
                || !profile.getResumeUrls().contains(request.getResumeUrl())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Selected resume does not belong to your profile."
            );
        }

        // 8. Convert request into application entity
        Application application = applicationMapper.toEntity(request);

        // 9. Set system-controlled fields
        application.setCandidate(candidate);
        application.setJob(job);
        application.setStatus(ApplicationStatus.APPLIED);

        // 10. Save and return response
        Application savedApplication = applicationRepository.save(application);

        return applicationMapper.toResponse(savedApplication);
    }

    @Transactional(readOnly = true)
    public Page<ApplicationResponse> getMyApplications(
            User candidate,
            ApplicationStatus status,
            Pageable pageable) {

        // 1. Verify candidate role
        if (candidate.getRole() != UserRole.CANDIDATE) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only candidates can view their applications."
            );
        }

        // 2. Fetch applications with optional status filter
        Page<Application> applications;

        if (status != null) {
            applications = applicationRepository.findByCandidate_IdAndStatus(
                    candidate.getId(), status, pageable
            );
        } else {
            applications = applicationRepository.findByCandidate_Id(
                    candidate.getId(), pageable
            );
        }

        // 3. Convert entities to response DTOs
        return applications.map(applicationMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public ApplicationResponse getApplicationById(
            Long applicationId, User user) {

        // 1. Find application
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Application does not exist."
                ));

        // 2. Candidate authorization
        if (user.getRole() == UserRole.CANDIDATE) {

            if (!(application.getCandidate().getId() == (user.getId()))) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "You are not authorized to view this application."
                );
            }

        }
        // 3. Recruiter authorization
        else if (user.getRole() == UserRole.RECRUITER) {

            Long companyId = application.getJob().getCompany().getId();

            boolean isCompanyRecruiter =
                    companyRecruiterRepository.existsByCompanyIdAndRecruiterId(
                            companyId, user.getId()
                    );

            if (!isCompanyRecruiter) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "You are not authorized to view this application."
                );
            }

        } else {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not authorized to view applications."
            );
        }

        // 4. Convert and return response
        return applicationMapper.toResponse(application);
    }


    @Transactional
    public void withdrawApplication(Long applicationId, User candidate) {

        // 1. Verify candidate role
        if (candidate.getRole() != UserRole.CANDIDATE) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only candidates can withdraw applications."
            );
        }

        // 2. Find application belonging to candidate
        Application application = applicationRepository
                .findByIdAndCandidate_Id(applicationId, candidate.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Application does not exist."
                ));

        // 3. Verify application status
        if (application.getStatus() != ApplicationStatus.APPLIED
                && application.getStatus() != ApplicationStatus.UNDER_REVIEW) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "This application cannot be withdrawn."
            );
        }

        // 4. Delete application
        applicationRepository.delete(application);
    }


    @Transactional(readOnly = true)
    public Page<ApplicationResponse> getJobApplications(
            Long jobId,
            User recruiter,
            ApplicationStatus status,
            Pageable pageable) {

        // 1. Verify recruiter role
        if (recruiter.getRole() != UserRole.RECRUITER) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only recruiters can view job applications."
            );
        }

        // 2. Find job
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Job does not exist."
                ));

        // 3. Verify recruiter belongs to the job's company
        boolean isCompanyRecruiter =
                companyRecruiterRepository.existsByCompanyIdAndRecruiterId(
                        job.getCompany().getId(),
                        recruiter.getId()
                );

        if (!isCompanyRecruiter) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not authorized to view applications for this job."
            );
        }

        // 4. Fetch applications with optional status filter
        Page<Application> applications;

        if (status != null) {
            applications = applicationRepository.findByJob_IdAndStatus(
                    jobId, status, pageable
            );
        } else {
            applications = applicationRepository.findByJob_Id(
                    jobId, pageable
            );
        }

        // 5. Convert entities to response DTOs
        return applications.map(applicationMapper::toResponse);
    }


    @Transactional
    public ApplicationResponse updateApplicationStatus(
            Long applicationId,
            User recruiter,
            UpdateApplicationStatusRequest request) {

        // 1. Verify recruiter role
        if (recruiter.getRole() != UserRole.RECRUITER) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only recruiters can update application status."
            );
        }

        // 2. Find and lock application
        Application application = applicationRepository.findByIdForUpdate(applicationId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Application does not exist."
                ));

        // 3. Verify recruiter belongs to the job's company
        Long companyId = application.getJob().getCompany().getId();

        if (!companyRecruiterRepository.existsByCompanyIdAndRecruiterId(
                companyId, recruiter.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not authorized to manage this application."
            );
        }

        // 4. Validate status transition
        ApplicationStatus currentStatus = application.getStatus();
        ApplicationStatus newStatus = request.getStatus();

        boolean validTransition = switch (currentStatus) {
            case APPLIED ->
                    newStatus == ApplicationStatus.UNDER_REVIEW ||
                            newStatus == ApplicationStatus.REJECTED;

            case UNDER_REVIEW ->
                    newStatus == ApplicationStatus.SHORTLISTED ||
                            newStatus == ApplicationStatus.REJECTED;

            case SHORTLISTED ->
                    newStatus == ApplicationStatus.HIRED ||
                            newStatus == ApplicationStatus.REJECTED;

            case REJECTED, HIRED -> false;
        };

        if (!validTransition) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid application status transition."
            );
        }

        // 5. Decrement vacancy when candidate is hired
        if (newStatus == ApplicationStatus.HIRED) {
            jobService.decrementVacancy(application.getJob().getId());
        }

        // 6. Update application status
        application.setStatus(newStatus);

        Application updatedApplication = applicationRepository.save(application);

        return applicationMapper.toResponse(updatedApplication);
    }
}