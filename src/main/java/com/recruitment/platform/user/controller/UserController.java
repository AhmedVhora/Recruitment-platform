package com.recruitment.platform.user.controller;

import com.recruitment.platform.user.dto.*;
import com.recruitment.platform.user.entity.User;
import com.recruitment.platform.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public LoginResponse loginUser(
            @Valid @RequestBody LoginRequest request) {

        return userService.login(request);
    }

    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse getCurrentUser(@AuthenticationPrincipal User user){
        return userService.getCurrentUser(user);
    }

    @PatchMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse updateCurrentUser(@AuthenticationPrincipal User user,
                                          @RequestBody UpdateUserRequest request){
        return userService.updateCurrentUser(user, request);
    }

    @PatchMapping("/me/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(@AuthenticationPrincipal User user,
                               @Valid @RequestBody ChangePasswordRequest request){
        userService.changePassword(user,request);
    }

    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAccount(@AuthenticationPrincipal User user,
                              @Valid @RequestBody DeleteAccountRequest request){
        userService.deleteAccount(user,request);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@RequestHeader("Authorization") String authHeader){
        String token =authHeader.substring(7).trim();
        userService.logOut(token);
    }
}

