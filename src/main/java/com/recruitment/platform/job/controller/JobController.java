package com.recruitment.platform.job.controller;

import com.recruitment.platform.job.dto.CreateJobRequest;
import com.recruitment.platform.job.dto.JobResponse;
import com.recruitment.platform.job.dto.JobSearchRequest;
import com.recruitment.platform.job.dto.UpdateJobRequest;
import com.recruitment.platform.job.service.JobService;
import com.recruitment.platform.user.entity.User;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/jobs")
public class JobController {
    private final JobService jobService;
    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public JobResponse createJob(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CreateJobRequest request) {

        return jobService.createJob(user, request);
    }

    @PatchMapping("/{jobId}")
    @ResponseStatus(HttpStatus.OK)
    public JobResponse updateJob(
            @PathVariable Long jobId,
            @AuthenticationPrincipal User user,
            @Valid @RequestBody UpdateJobRequest request) {

        return jobService.updateJob(jobId, user, request);
    }


    @DeleteMapping("/{jobId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteJob(
            @PathVariable Long jobId,
            @AuthenticationPrincipal User user) {

        jobService.deleteJob(jobId, user);
    }

    @GetMapping("/{jobId}")
    @ResponseStatus(HttpStatus.OK)
    public JobResponse getJobById(
            @PathVariable Long jobId,
            @AuthenticationPrincipal User user) {

        return jobService.getJobById(jobId, user);
    }

    @GetMapping("/company/{companyId}")
    @ResponseStatus(HttpStatus.OK)
    public Page<JobResponse> getCompanyJobs(
            @PathVariable Long companyId,
            @AuthenticationPrincipal User user,
            Pageable pageable) {

        return jobService.getCompanyJobs(companyId, user, pageable);
    }

    @PatchMapping("/{jobId}/publish")
    @ResponseStatus(HttpStatus.OK)
    public JobResponse publishJob(
            @PathVariable Long jobId,
            @AuthenticationPrincipal User user) {

        return jobService.publishJob(jobId, user);
    }

    @PatchMapping("/{jobId}/close")
    @ResponseStatus(HttpStatus.OK)
    public JobResponse closeJob(
            @PathVariable Long jobId,
            @AuthenticationPrincipal User user) {

        return jobService.closeJob(jobId, user);
    }

    @PatchMapping("/{jobId}/reopen")
    @ResponseStatus(HttpStatus.OK)
    public JobResponse reopenJob(
            @PathVariable Long jobId,
            @AuthenticationPrincipal User user) {

        return jobService.reopenJob(jobId, user);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<JobResponse> searchJobs(
            @Valid @ModelAttribute JobSearchRequest request) {

        return jobService.searchJobs(request);
    }

}
