package com.recruitment.platform.user;

import com.recruitment.platform.security.JwtService;
import com.recruitment.platform.security.TokenRevocationService;
import com.recruitment.platform.user.dto.ChangePasswordRequest;
import com.recruitment.platform.user.dto.RegisterUserRequest;
import com.recruitment.platform.user.dto.UserResponse;
import com.recruitment.platform.user.entity.User;
import com.recruitment.platform.user.entity.UserRole;
import com.recruitment.platform.user.mapper.UserMapper;
import com.recruitment.platform.user.repository.UserRepository;
import com.recruitment.platform.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock private UserRepository userRepository;
    @Mock private UserMapper userMapper;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @Mock private TokenRevocationService tokenRevocationService;
    @InjectMocks private UserService userService;

    @Test
    void registrationNormalizesEmailAndHashesPassword() {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setFullName("Candidate");
        request.setEmail("  Candidate@Example.COM ");
        request.setPassword("password123");
        request.setRole(UserRole.CANDIDATE);
        User user = new User();
        UserResponse response = new UserResponse();

        when(userRepository.existsByEmail("candidate@example.com")).thenReturn(false);
        when(userMapper.toEntity(request)).thenReturn(user);
        when(passwordEncoder.encode("password123")).thenReturn("hashed");
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(response);

        assertSame(response, userService.createUser(request));
        assertEquals("candidate@example.com", request.getEmail());
        assertEquals("hashed", user.getPassword());
        verify(userRepository).existsByEmail("candidate@example.com");
    }

    @Test
    void registrationRejectsDuplicateNormalizedEmail() {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setEmail(" DUPLICATE@example.com ");
        request.setRole(UserRole.CANDIDATE);
        when(userRepository.existsByEmail("duplicate@example.com")).thenReturn(true);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> userService.createUser(request));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        verifyNoInteractions(userMapper, passwordEncoder);
    }

    @Test
    void registrationRejectsAdminRole() {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setRole(UserRole.ADMIN);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> userService.createUser(request));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verifyNoInteractions(userRepository);
    }

    @Test
    void passwordChangeRejectsReuseOfCurrentPassword() {
        User user = new User();
        user.setPassword("hash");
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("current");
        request.setNewPassword("current");
        when(passwordEncoder.matches("current", "hash")).thenReturn(true);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> userService.changePassword(user, request));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(userRepository, never()).save(any());
    }
}
