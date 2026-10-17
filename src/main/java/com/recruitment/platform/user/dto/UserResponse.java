package com.recruitment.platform.user.dto;

import com.recruitment.platform.user.entity.UserRole;

public class UserResponse {
    private long id;
    private String email;
    private String fullName;
    private UserRole role;

    public UserResponse(long id, String email, String fullName, UserRole role) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
    }

    public UserResponse(){

    }

    public long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    public UserRole getRole() {
        return role;
    }
}
