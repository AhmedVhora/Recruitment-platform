package com.recruitment.platform.company;

import com.recruitment.platform.company.dto.CreateCompanyRequest;
import com.recruitment.platform.company.entity.Company;
import com.recruitment.platform.company.entity.CompanyRecruiter;
import com.recruitment.platform.company.mapper.CompanyMapper;
import com.recruitment.platform.company.repository.CompanyMembershipRequestRepository;
import com.recruitment.platform.company.repository.CompanyRecruiterRepository;
import com.recruitment.platform.company.repository.CompanyRepository;
import com.recruitment.platform.company.service.CompanyService;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {
    @Mock private CompanyRepository companyRepository;
    @Mock private CompanyRecruiterRepository companyRecruiterRepository;
    @Mock private CompanyMembershipRequestRepository companyMembershipRequestRepository;
    @Mock private UserRepository userRepository;
    @Mock private CompanyMapper companyMapper;
    @InjectMocks private CompanyService companyService;

    @Test
    void candidateCannotCreateCompany() {
        User candidate = new User();
        candidate.setRole(UserRole.CANDIDATE);
        CreateCompanyRequest request = new CreateCompanyRequest();
        request.setName("Acme");

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> companyService.createCompany(request, candidate));

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        verifyNoInteractions(companyRepository);
    }

    @Test
    void duplicateNormalizedCompanyNameIsRejected() {
        User recruiter = new User();
        recruiter.setRole(UserRole.RECRUITER);
        CreateCompanyRequest request = new CreateCompanyRequest();
        request.setName("  AcMe  ");
        when(companyRepository.existsByNormalizedName("acme")).thenReturn(true);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> companyService.createCompany(request, recruiter));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        verifyNoInteractions(companyMapper, companyRecruiterRepository);
    }
}
