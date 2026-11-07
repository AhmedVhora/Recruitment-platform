package com.recruitment.platform.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class RevokedTokenRepositoryTest {
    @Autowired
    private RevokedTokenRepository revokedTokenRepository;

    @Test
    void cleanupDeletesOnlyExpiredTokens() {
        revokedTokenRepository.save(new RevokedToken("expired", Instant.now().minusSeconds(60)));
        revokedTokenRepository.save(new RevokedToken("active", Instant.now().plusSeconds(60)));

        revokedTokenRepository.deleteByExpiresAtBefore(Instant.now());

        assertFalse(revokedTokenRepository.existsById("expired"));
        assertTrue(revokedTokenRepository.existsById("active"));
    }
}
