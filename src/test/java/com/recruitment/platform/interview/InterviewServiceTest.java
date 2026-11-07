package com.recruitment.platform.interview;

import com.recruitment.platform.application.entity.Application;
import com.recruitment.platform.application.repository.ApplicationRepository;
import com.recruitment.platform.application.service.ApplicationService;
import com.recruitment.platform.company.entity.Company;
import com.recruitment.platform.company.repository.CompanyRecruiterRepository;
import com.recruitment.platform.interview.dto.UpdateInterviewRequest;
import com.recruitment.platform.interview.entity.InterviewProcess;
import com.recruitment.platform.interview.entity.InterviewRound;
import com.recruitment.platform.interview.entity.enums.InterviewRoundStatus;
import com.recruitment.platform.interview.mapper.InterviewMapper;
import com.recruitment.platform.interview.repository.InterviewProcessRepository;
import com.recruitment.platform.interview.repository.InterviewRoundRepository;
import com.recruitment.platform.interview.service.InterviewService;
import com.recruitment.platform.job.entity.Job;
import com.recruitment.platform.job.repository.JobRepository;
import com.recruitment.platform.user.entity.User;
import com.recruitment.platform.user.entity.UserRole;
import com.recruitment.platform.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InterviewServiceTest {

    @Mock private InterviewProcessRepository interviewProcessRepository;
    @Mock private InterviewRoundRepository interviewRoundRepository;
    @Mock private ApplicationRepository applicationRepository;
    @Mock private UserRepository userRepository;
    @Mock private CompanyRecruiterRepository companyRecruiterRepository;
    @Mock private InterviewMapper interviewMapper;
    @Mock private JobRepository jobRepository;
    @Mock private ApplicationService applicationService;

    @InjectMocks
    private InterviewService interviewService;

    @Test
    void cancelledRoundCannotBeReopenedWhenALaterRoundExists() {
        User recruiter = new User();
        recruiter.setRole(UserRole.RECRUITER);

        InterviewRound cancelledRound = org.mockito.Mockito.mock(InterviewRound.class);
        InterviewProcess process = org.mockito.Mockito.mock(InterviewProcess.class);
        Application application = org.mockito.Mockito.mock(Application.class);
        Job job = org.mockito.Mockito.mock(Job.class);
        Company company = org.mockito.Mockito.mock(Company.class);

        when(interviewRoundRepository.findById(11L))
                .thenReturn(Optional.of(cancelledRound));
        when(cancelledRound.getStatus()).thenReturn(InterviewRoundStatus.CANCELLED);
        when(cancelledRound.getInterviewProcess()).thenReturn(process);
        when(cancelledRound.getRoundNumber()).thenReturn(1);
        when(process.getId()).thenReturn(7L);
        when(process.getApplication()).thenReturn(application);
        when(application.getJob()).thenReturn(job);
        when(job.getCompany()).thenReturn(company);
        when(company.getId()).thenReturn(3L);
        when(companyRecruiterRepository.existsByCompanyIdAndRecruiterId(3L, recruiter.getId()))
                .thenReturn(true);
        when(interviewRoundRepository
                .existsByInterviewProcessIdAndRoundNumberGreaterThan(7L, 1))
                .thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> interviewService.updateInterview(
                        11L, recruiter, new UpdateInterviewRequest())
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }
}
