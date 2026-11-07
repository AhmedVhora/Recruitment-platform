package com.recruitment.platform.security;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;

public interface RevokedTokenRepository extends JpaRepository<RevokedToken,String> {
    boolean existsByJti(String jti);

    @Transactional
    void deleteByExpiresAtBefore(Instant instant);
}
