package com.recruitment.platform.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TokenCleanupSchedulerTest {

    @Mock
    private RevokedTokenRepository revokedTokenRepository;

    @InjectMocks
    private TokenCleanupScheduler tokenCleanupScheduler;

    @Test
    void cleanupExpiredTokensDeletesOnlyTokensExpiredBeforeNow() {
        tokenCleanupScheduler.cleanupExpiredTokens();

        verify(revokedTokenRepository).deleteByExpiresAtBefore(any(Instant.class));
    }
}
