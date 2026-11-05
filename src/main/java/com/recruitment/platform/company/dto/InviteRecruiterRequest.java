package com.recruitment.platform.company.dto;

import jakarta.validation.constraints.NotNull;


public class InviteRecruiterRequest {

    @NotNull(message = "Recruiter ID is required")
    private Long recruiterId;

    public Long getRecruiterId() {
        return recruiterId;
    }

    public void setRecruiterId( Long recruiterId) {
        this.recruiterId = recruiterId;
    }
}