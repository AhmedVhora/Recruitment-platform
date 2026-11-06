package com.recruitment.platform.application.controller;

import com.recruitment.platform.application.dto.ApplicationResponse;
import com.recruitment.platform.application.dto.ApplyJobRequest;
import com.recruitment.platform.application.dto.UpdateApplicationStatusRequest;
import com.recruitment.platform.application.entity.enums.ApplicationStatus;
import com.recruitment.platform.application.service.ApplicationService;
import com.recruitment.platform.user.entity.User;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;

    // 1. Apply for a job
    @PostMapping("/jobs/{jobId}/applications")
    @ResponseStatus(HttpStatus.CREATED)
    public ApplicationResponse applyToJob(
            @PathVariable Long jobId,
            @AuthenticationPrincipal User candidate,
            @Valid @RequestBody ApplyJobRequest request) {

        return applicationService.applyToJob(jobId, candidate, request);
    }

    // 2. Get authenticated candidate's applications
    @GetMapping("/applications/me")
    @ResponseStatus(HttpStatus.OK)
    public Page<ApplicationResponse> getMyApplications(
            @AuthenticationPrincipal User candidate,
            @RequestParam(required = false) ApplicationStatus status,
            Pageable pageable) {

        return applicationService.getMyApplications(
                candidate, status, pageable
        );
    }

    // 3. Get application by ID
    @GetMapping("/applications/{applicationId}")
    @ResponseStatus(HttpStatus.OK)
    public ApplicationResponse getApplicationById(
            @PathVariable Long applicationId,
            @AuthenticationPrincipal User user) {

        return applicationService.getApplicationById(applicationId, user);
    }


    // 4. Withdraw application
    @DeleteMapping("/applications/{applicationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void withdrawApplication(
            @PathVariable Long applicationId,
            @AuthenticationPrincipal User candidate) {

        applicationService.withdrawApplication(applicationId, candidate);
    }

    // 5. Get applications for a specific job
    @GetMapping("/jobs/{jobId}/applications")
    @ResponseStatus(HttpStatus.OK)
    public Page<ApplicationResponse> getJobApplications(
            @PathVariable Long jobId,
            @AuthenticationPrincipal User recruiter,
            @RequestParam(required = false) ApplicationStatus status,
            Pageable pageable) {

        return applicationService.getJobApplications(
                jobId, recruiter, status, pageable
        );
    }

    @PatchMapping("/applications/{applicationId}/status")
    @ResponseStatus(HttpStatus.OK)
    public ApplicationResponse updateApplicationStatus(
            @PathVariable Long applicationId,
            @AuthenticationPrincipal User recruiter,
            @Valid @RequestBody UpdateApplicationStatusRequest request) {

        return applicationService.updateApplicationStatus(
                applicationId, recruiter, request
        );
    }
}