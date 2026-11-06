package com.recruitment.platform.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ApplyJobRequest {

    @NotBlank(message = "Resume URL is required")
    @Size(max = 2048, message = "Resume URL cannot exceed 2048 characters")
    private String resumeUrl;

    public String getResumeUrl() {
        return resumeUrl;
    }

    public void setResumeUrl(String resumeUrl) {
        this.resumeUrl = resumeUrl;
    }
}