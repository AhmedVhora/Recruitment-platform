package com.recruitment.platform.application.mapper;

import com.recruitment.platform.application.dto.ApplyJobRequest;
import com.recruitment.platform.application.dto.ApplicationResponse;
import com.recruitment.platform.application.entity.Application;
import org.springframework.stereotype.Component;

@Component
public class ApplicationMapper {

    public ApplicationResponse toResponse(Application application) {

        ApplicationResponse response = new ApplicationResponse();

        response.setId(application.getId());
        response.setJobId(application.getJob().getId());
        response.setJobTitle(application.getJob().getTitle());
        response.setStatus(application.getStatus());
        response.setResumeUrl(application.getResumeUrl());
        response.setAppliedAt(application.getAppliedAt());
        response.setUpdatedAt(application.getUpdatedAt());

        return response;
    }

    public Application toEntity(ApplyJobRequest request) {

        Application application = new Application();

        application.setResumeUrl(request.getResumeUrl());

        return application;
    }
}