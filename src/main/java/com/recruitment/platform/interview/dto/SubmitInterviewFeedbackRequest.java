
package com.recruitment.platform.interview.dto;

import com.recruitment.platform.interview.entity.enums.InterviewOutcome;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class SubmitInterviewFeedbackRequest {

    @NotBlank
    private String feedback;

    @Size(max = 2000)
    private String strengths;

    @Size(max = 2000)
    private String areasForImprovement;

    @NotNull
    private InterviewOutcome outcome;

    // Generate getters and setters for all fields


    public String getFeedback() {
        return feedback;
    }

    public String getStrengths() {
        return strengths;
    }

    public String getAreasForImprovement() {
        return areasForImprovement;
    }

    public InterviewOutcome getOutcome() {
        return outcome;
    }
}
