package com.recruitment.platform.user.service;

import com.recruitment.platform.security.JwtService;
import com.recruitment.platform.security.TokenRevocationService;
import com.recruitment.platform.user.dto.*;
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
    @Autowired
    private TokenRevocationService tokenRevocationService;

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


    public UserResponse getCurrentUser(User user){
        return userMapper.toResponse(user);
    }

    public UserResponse updateCurrentUser(
            User user,
            UpdateUserRequest request) {


        boolean changed = false;

        if (request.getFullName() != null) {
            String fullName = request.getFullName().trim();

            if (fullName.isBlank()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Full name cannot be blank"
                );
            }

            if (!user.getFullName().equals(fullName)) {
                user.setFullName(fullName);
                changed = true;
            }
        }

        if (request.getRole() != null) {
            UserRole role = request.getRole();

            if (role == UserRole.ADMIN) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "You cannot assign yourself the ADMIN role"
                );
            }

            if (user.getRole() != role) {
                user.setRole(role);
                changed = true;
            }
        }

        if (!changed) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No changes provided"
            );
        }

        User savedUser = userRepository.save(user);

        return userMapper.toResponse(savedUser);
    }

    public void changePassword(User user,ChangePasswordRequest request){
        String currentPassword = request.getCurrentPassword();
        String newPassword = request.getNewPassword();
        if(!passwordEncoder.matches(currentPassword,user.getPassword())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Incorrect password");
        }
        if(passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New password cannot be the same as old password");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public void deleteAccount(User user, DeleteAccountRequest request){
        String password = request.getPassword();
        if(!passwordEncoder.matches(password, user.getPassword())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Incorrect password");
        }

        userRepository.delete(user);
    }

    public void logOut(String token){
        tokenRevocationService.revokeToken(token);
    }

}
