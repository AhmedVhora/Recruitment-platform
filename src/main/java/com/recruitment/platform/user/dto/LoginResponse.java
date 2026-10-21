package com.recruitment.platform.user.dto;

public class LoginResponse {
    private String accessToken;
    private String tokenType;
    private UserResponse user;


    public LoginResponse(String accessToken, String tokenType, UserResponse user) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.user = user;
    }

    public LoginResponse() {
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public UserResponse getUser() {
        return user;
    }
}
