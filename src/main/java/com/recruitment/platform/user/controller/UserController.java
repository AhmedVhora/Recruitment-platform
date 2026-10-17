package com.recruitment.platform.user.controller;

import com.recruitment.platform.user.dto.RegisterUserRequest;
import com.recruitment.platform.user.dto.UserResponse;
import com.recruitment.platform.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse registerUser(
           @Valid @RequestBody RegisterUserRequest request) {

        return userService.createUser(request);
    }
}

