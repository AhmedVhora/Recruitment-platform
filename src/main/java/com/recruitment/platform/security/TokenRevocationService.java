package com.recruitment.platform.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
@Service
public class TokenRevocationService {
    @Autowired
    JwtService jwtService;
    @Autowired
    RevokedTokenRepository revokedTokenRepository;
    public void revokeToken(String token){
        String jti = jwtService.getJti(token);
        Instant expiration = jwtService.getExpiration(token);
        RevokedToken tk = new RevokedToken(jti,expiration);
        revokedTokenRepository.save(tk);
    }

    public boolean isTokenRevoked(String jti){
        return revokedTokenRepository.existsByJti(jti);
    }
}
