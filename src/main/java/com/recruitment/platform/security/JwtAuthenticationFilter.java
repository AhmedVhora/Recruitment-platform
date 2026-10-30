
package com.recruitment.platform.security;

import com.recruitment.platform.user.entity.User;
import com.recruitment.platform.user.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {


    private final TokenRevocationService tokenRevocationService;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(TokenRevocationService tokenRevocationService, JwtService jwtService, UserRepository userRepository) {
        this.tokenRevocationService = tokenRevocationService;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // No credentials: let Spring Security decide whether access is allowed.
        if (authHeader == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // This API expects Bearer authentication when Authorization is supplied.
        if (!authHeader.regionMatches(true, 0, "Bearer ", 0, 7)) {
            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Invalid Authorization header"
            );
            return;
        }

        String token = authHeader.substring(7).trim();

        if (token.isEmpty()) {
            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Missing authentication token"
            );
            return;
        }

        try {
            String jti = jwtService.getJti(token);

            if (tokenRevocationService.isTokenRevoked(jti)) {
                response.sendError(
                        HttpServletResponse.SC_UNAUTHORIZED,
                        "Token has been revoked"
                );
                return;
            }
            Long userId = jwtService.extractUserId(token);

            User user = userRepository.findById(userId)
                    .orElse(null);

            if (user == null) {
                response.sendError(
                        HttpServletResponse.SC_UNAUTHORIZED,
                        "Invalid authentication token"
                );
                return;
            }

            if (SecurityContextHolder.getContext().getAuthentication() == null) {

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                user,
                                null,
                                List.of(
                                        new SimpleGrantedAuthority(
                                                "ROLE_" + user.getRole().name()
                                        )
                                )
                        );

                SecurityContextHolder.getContext()
                        .setAuthentication(authentication);
            }

        } catch (JwtException | IllegalArgumentException e) {
            SecurityContextHolder.clearContext();

            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Invalid or expired authentication token"
            );
            return;
        }

        filterChain.doFilter(request, response);
    }
}
