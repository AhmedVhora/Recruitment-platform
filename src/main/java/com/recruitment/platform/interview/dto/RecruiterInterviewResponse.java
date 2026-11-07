
package com.recruitment.platform.interview.dto;

import com.recruitment.platform.interview.entity.enums.InterviewMode;
import com.recruitment.platform.interview.entity.enums.InterviewOutcome;
import com.recruitment.platform.interview.entity.enums.InterviewRoundStatus;

import java.time.LocalDateTime;

public class RecruiterInterviewResponse {

    private Long id;
    private Long applicationId;
    private Integer roundNumber;
    private Integer totalRounds;
    private String roundName;

    private LocalDateTime scheduledAt;
    private Integer durationMinutes;

    private InterviewMode mode;
    private String meetingLink;
    private String location;

    private Long interviewerId;
    private String interviewerName;

    private InterviewRoundStatus status;
    private InterviewOutcome outcome;

    private String feedback;
    private String strengths;
    private String areasForImprovement;
    private LocalDateTime feedbackSubmittedAt;

    private String cancellationReason;
    private LocalDateTime cancelledAt;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public Integer getRoundNumber() {
        return roundNumber;
    }

    public void setRoundNumber(Integer roundNumber) {
        this.roundNumber = roundNumber;
    }

    public Integer getTotalRounds() {
        return totalRounds;
    }

    public void setTotalRounds(Integer totalRounds) {
        this.totalRounds = totalRounds;
    }

    public String getRoundName() {
        return roundName;
    }

    public void setRoundName(String roundName) {
        this.roundName = roundName;
    }

    public LocalDateTime getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(LocalDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public InterviewMode getMode() {
        return mode;
    }

    public void setMode(InterviewMode mode) {
        this.mode = mode;
    }

    public String getMeetingLink() {
        return meetingLink;
    }

    public void setMeetingLink(String meetingLink) {
        this.meetingLink = meetingLink;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Long getInterviewerId() {
        return interviewerId;
    }

    public void setInterviewerId(Long interviewerId) {
        this.interviewerId = interviewerId;
    }

    public String getInterviewerName() {
        return interviewerName;
    }

    public void setInterviewerName(String interviewerName) {
        this.interviewerName = interviewerName;
    }

    public InterviewRoundStatus getStatus() {
        return status;
    }

    public void setStatus(InterviewRoundStatus status) {
        this.status = status;
    }

    public InterviewOutcome getOutcome() {
        return outcome;
    }

    public void setOutcome(InterviewOutcome outcome) {
        this.outcome = outcome;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getStrengths() {
        return strengths;
    }

    public void setStrengths(String strengths) {
        this.strengths = strengths;
    }

    public String getAreasForImprovement() {
        return areasForImprovement;
    }

    public void setAreasForImprovement(String areasForImprovement) {
        this.areasForImprovement = areasForImprovement;
    }

    public LocalDateTime getFeedbackSubmittedAt() {
        return feedbackSubmittedAt;
    }

    public void setFeedbackSubmittedAt(LocalDateTime feedbackSubmittedAt) {
        this.feedbackSubmittedAt = feedbackSubmittedAt;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(LocalDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
