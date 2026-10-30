package com.recruitment.platform.user.dto;

import com.recruitment.platform.user.entity.UserRole;

public class UpdateUserRequest {
    private String fullName;
    private UserRole role;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}
