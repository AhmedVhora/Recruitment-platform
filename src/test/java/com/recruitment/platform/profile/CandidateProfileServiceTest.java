package com.recruitment.platform.profile;

import com.recruitment.platform.application.repository.ApplicationRepository;
import com.recruitment.platform.profile.repository.CandidateProfileRepository;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CandidateProfileServiceTest {

    @Mock
    private CandidateProfileRepository candidateProfileRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    @InjectMocks
    private CandidateProfileService candidateProfileService;

    @Test
    void profileWithApplicationHistoryCannotBeDeleted() {
        User candidate = new User();
        candidate.setRole(UserRole.CANDIDATE);

        when(applicationRepository.existsByCandidate_Id(candidate.getId()))
                .thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> candidateProfileService.deleteProfile(candidate)
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        verifyNoInteractions(candidateProfileRepository);
    }
}
