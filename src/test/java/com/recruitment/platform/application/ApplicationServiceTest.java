package com.recruitment.platform.application;

import com.recruitment.platform.application.dto.ApplyJobRequest;
import com.recruitment.platform.application.entity.Application;
import com.recruitment.platform.application.entity.enums.ApplicationStatus;
import com.recruitment.platform.application.mapper.ApplicationMapper;
import com.recruitment.platform.application.repository.ApplicationRepository;
import com.recruitment.platform.application.service.ApplicationService;
import com.recruitment.platform.company.repository.CompanyRecruiterRepository;
import com.recruitment.platform.job.entity.Job;
import com.recruitment.platform.job.entity.enums.JobStatus;
import com.recruitment.platform.job.repository.JobRepository;
import com.recruitment.platform.job.service.JobService;
import com.recruitment.platform.profile.service.CandidateProfileService;
import com.recruitment.platform.user.entity.User;
import com.recruitment.platform.user.entity.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {
    @Mock private JobService jobService;
    @Mock private CompanyRecruiterRepository companyRecruiterRepository;
    @Mock private CandidateProfileService candidateProfileService;
    @Mock private ApplicationRepository applicationRepository;
    @Mock private JobRepository jobRepository;
    @Mock private ApplicationMapper applicationMapper;
    @InjectMocks private ApplicationService applicationService;

    @Test
    void recruiterCannotApplyForJob() {
        User recruiter = new User();
        recruiter.setRole(UserRole.RECRUITER);
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> applicationService.applyToJob(1L, recruiter, request()));
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        verifyNoInteractions(jobRepository);
    }

    @Test
    void applicationToUnpublishedJobIsRejected() {
        User candidate = new User();
        candidate.setRole(UserRole.CANDIDATE);
        Job job = new Job();
        job.setStatus(JobStatus.DRAFT);
        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> applicationService.applyToJob(1L, candidate, request()));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verifyNoInteractions(applicationRepository, candidateProfileService);
    }

    @Test
    void shortlistedApplicationCannotBeWithdrawn() {
        User candidate = new User();
        candidate.setRole(UserRole.CANDIDATE);
        Application application = new Application();
        application.setStatus(ApplicationStatus.SHORTLISTED);
        when(applicationRepository.findByIdAndCandidate_Id(7L, candidate.getId()))
                .thenReturn(Optional.of(application));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> applicationService.withdrawApplication(7L, candidate));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(applicationRepository, never()).delete(any());
    }

    private ApplyJobRequest request() {
        ApplyJobRequest request = new ApplyJobRequest();
        request.setResumeUrl("https://example.test/resume.pdf");
        return request;
    }
}
