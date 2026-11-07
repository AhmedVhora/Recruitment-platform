package com.recruitment.platform.security;

import com.recruitment.platform.user.entity.User;
import com.recruitment.platform.user.entity.UserRole;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {
    private static final String SECRET = "dGVzdC1vbmx5LXNlY3JldC1rZXktbXVzdC1iZS1hdC1sZWFzdC0zMi1ieXRlcy1sb25n";

    @Test
    void generatedTokenContainsAndValidatesTheCurrentUser() {
        JwtService jwtService = configuredService(60_000);
        User user = new User();
        user.setEmail("candidate@example.com");
        user.setRole(UserRole.CANDIDATE);

        String token = jwtService.generateToken(user);

        assertTrue(jwtService.isTokenValid(token, user));
        assertNotNull(jwtService.getJti(token));
        assertTrue(jwtService.getExpiration(token).isAfter(java.time.Instant.now()));
    }

    @Test
    void malformedTokenIsRejected() {
        JwtService jwtService = configuredService(60_000);

        assertThrows(JwtException.class, () -> jwtService.extractUserId("not-a-jwt"));
    }

    private JwtService configuredService(long expiration) {
        JwtService jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", SECRET);
        ReflectionTestUtils.setField(jwtService, "expiration", expiration);
        return jwtService;
    }
}
