
package com.recruitment.platform.interview.controller;

import com.recruitment.platform.interview.dto.*;
import com.recruitment.platform.interview.service.InterviewService;
import com.recruitment.platform.user.entity.User;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/interviews")
public class InterviewController {

    @Autowired
    private InterviewService interviewService;

    // API 1: Create an interview round
    @PostMapping("/applications/{applicationId}")
    @ResponseStatus(HttpStatus.CREATED)
    public RecruiterInterviewResponse createInterviewRound(
            @PathVariable Long applicationId,
            @AuthenticationPrincipal User recruiter,
            @Valid @RequestBody CreateInterviewRoundRequest request) {

        return interviewService.createInterviewRound(
                applicationId,
                recruiter,
                request
        );
    }


    // API 2: Get interviews of the authenticated user
    @GetMapping("/me")
    public Page<?> getMyInterviews(
            @AuthenticationPrincipal User user,
            Pageable pageable) {

        return interviewService.getMyInterviews(user, pageable);
    }

    // API 3: Get interviews associated with a job
    @GetMapping("/jobs/{jobId}")
    public Page<RecruiterInterviewResponse> getJobInterviews(
            @PathVariable Long jobId,
            @AuthenticationPrincipal User recruiter,
            Pageable pageable) {

        return interviewService.getJobInterviews(
                jobId,
                recruiter,
                pageable
        );
    }

    // API 4: Get interview details
    @GetMapping("/{interviewId}")
    public Object getInterviewById(
            @PathVariable Long interviewId,
            @AuthenticationPrincipal User user) {

        return interviewService.getInterviewById(interviewId, user);
    }


    // API 5: Update/reschedule an interview
    @PatchMapping("/{interviewId}")
    @ResponseStatus(HttpStatus.OK)
    public RecruiterInterviewResponse updateInterview(
            @PathVariable Long interviewId,
            @AuthenticationPrincipal User recruiter,
            @Valid @RequestBody UpdateInterviewRequest request) {

        return interviewService.updateInterview(
                interviewId,
                recruiter,
                request
        );
    }


    // API 6: Cancel an interview
    @PatchMapping("/{interviewId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public RecruiterInterviewResponse cancelInterview(
            @PathVariable Long interviewId,
            @AuthenticationPrincipal User recruiter,
            @Valid @RequestBody CancelInterviewRequest request) {

        return interviewService.cancelInterview(
                interviewId,
                recruiter,
                request
        );
    }



    // API 7: Submit interview feedback
    @PatchMapping("/{interviewId}/feedback")
    @ResponseStatus(HttpStatus.OK)
    public RecruiterInterviewResponse submitInterviewFeedback(
            @PathVariable Long interviewId,
            @AuthenticationPrincipal User interviewer,
            @Valid @RequestBody SubmitInterviewFeedbackRequest request) {

        return interviewService.submitInterviewFeedback(
                interviewId,
                interviewer,
                request
        );
    }




}
