package com.recruitment.platform.user.service;

import com.recruitment.platform.security.JwtService;
import com.recruitment.platform.user.dto.LoginRequest;
import com.recruitment.platform.user.dto.LoginResponse;
import com.recruitment.platform.user.dto.RegisterUserRequest;
import com.recruitment.platform.user.dto.UserResponse;
import com.recruitment.platform.user.entity.User;
import com.recruitment.platform.user.entity.UserRole;
import com.recruitment.platform.user.mapper.UserMapper;
import com.recruitment.platform.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;

    public UserResponse createUser(RegisterUserRequest request){
        if(request.getRole()==null || request.getRole()==UserRole.ADMIN){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid Role, Public Registration allows Candidate or Recruiter Role only."
            );
        }

        String normalizedEmail = request.getEmail()
                .trim()
                .toLowerCase(Locale.ROOT);

        request.setEmail(normalizedEmail);

        if(userRepository.existsByEmail(request.getEmail())){
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "An account with this email already exists"
            );
        }
        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);

    }

    public LoginResponse login(LoginRequest request) {
        String normalizedEmail = request.getEmail()
                .trim()
                .toLowerCase(Locale.ROOT);

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Invalid email or password"
                ));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid email or password"
            );
        }

        String token = jwtService.generateToken(user);

        UserResponse userResponse = userMapper.toResponse(user);

        return new LoginResponse(token, "Bearer", userResponse);
    }
}
