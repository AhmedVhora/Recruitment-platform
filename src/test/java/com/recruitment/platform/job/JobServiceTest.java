package com.recruitment.platform.job;

import com.recruitment.platform.company.entity.Company;
import com.recruitment.platform.company.repository.CompanyRecruiterRepository;
import com.recruitment.platform.company.repository.CompanyRepository;
import com.recruitment.platform.job.dto.CreateJobRequest;
import com.recruitment.platform.job.entity.enums.EmploymentType;
import com.recruitment.platform.job.entity.enums.WorkMode;
import com.recruitment.platform.job.mapper.JobMapper;
import com.recruitment.platform.job.repository.JobRepository;
import com.recruitment.platform.job.service.JobService;
import com.recruitment.platform.profile.mapper.CandidateProfileMapper;
import com.recruitment.platform.profile.repository.CandidateProfileRepository;
import com.recruitment.platform.user.entity.User;
import com.recruitment.platform.user.entity.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobServiceTest {
    @Mock private CompanyRecruiterRepository companyRecruiterRepository;
    @Mock private CompanyRepository companyRepository;
    @Mock private JobMapper jobMapper;
    @Mock private JobRepository jobRepository;
    @Mock private CandidateProfileRepository candidateProfileRepository;
    @Mock private CandidateProfileMapper candidateProfileMapper;
    @InjectMocks private JobService jobService;

    @Test
    void candidateCannotCreateJob() {
        User candidate = new User();
        candidate.setRole(UserRole.CANDIDATE);
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> jobService.createJob(candidate, validRequest()));
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        verifyNoInteractions(companyRepository);
    }

    @Test
    void invalidSalaryRangeIsRejectedForCompanyMember() {
        User recruiter = new User();
        recruiter.setRole(UserRole.RECRUITER);
        Company company = mock(Company.class);
        when(company.getId()).thenReturn(1L);
        CreateJobRequest request = validRequest();
        request.setMinSalary(new BigDecimal("100"));
        request.setMaxSalary(new BigDecimal("99"));
        when(companyRepository.findById(1L)).thenReturn(java.util.Optional.of(company));
        when(companyRecruiterRepository.existsByCompanyIdAndRecruiterId(1L, recruiter.getId())).thenReturn(true);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> jobService.createJob(recruiter, request));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verifyNoInteractions(jobMapper, jobRepository);
    }

    private CreateJobRequest validRequest() {
        return new CreateJobRequest(1L, "Developer", "Engineering", "Build services", null,
                Set.of("Java"), null, 0, EmploymentType.FULL_TIME, WorkMode.REMOTE,
                null, null, null, 1, LocalDate.now().plusDays(1));
    }
}
