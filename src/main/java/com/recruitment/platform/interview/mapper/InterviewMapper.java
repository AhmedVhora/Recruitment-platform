
package com.recruitment.platform.interview.mapper;

import com.recruitment.platform.interview.dto.CreateInterviewRoundRequest;
import com.recruitment.platform.interview.dto.CandidateInterviewResponse;
import com.recruitment.platform.interview.dto.RecruiterInterviewResponse;
import com.recruitment.platform.interview.entity.InterviewRound;
import org.springframework.stereotype.Component;

@Component
public class InterviewMapper {

    public InterviewRound toEntity(CreateInterviewRoundRequest request) {

        InterviewRound round = new InterviewRound();

        round.setRoundName(request.getRoundName());
        round.setScheduledAt(request.getScheduledAt());
        round.setDurationMinutes(request.getDurationMinutes());
        round.setMode(request.getMode());
        round.setMeetingLink(request.getMeetingLink());
        round.setLocation(request.getLocation());

        return round;
    }

    public RecruiterInterviewResponse toRecruiterResponse(InterviewRound round) {

        RecruiterInterviewResponse response = new RecruiterInterviewResponse();

        response.setId(round.getId());
        response.setApplicationId(
                round.getInterviewProcess().getApplication().getId()
        );
        response.setRoundNumber(round.getRoundNumber());
        response.setTotalRounds(round.getInterviewProcess().getTotalRounds());
        response.setRoundName(round.getRoundName());

        response.setScheduledAt(round.getScheduledAt());
        response.setDurationMinutes(round.getDurationMinutes());

        response.setMode(round.getMode());
        response.setMeetingLink(round.getMeetingLink());
        response.setLocation(round.getLocation());

        response.setInterviewerId(round.getInterviewer().getId());
        response.setInterviewerName(
                round.getInterviewer().getFullName()
        );

        response.setStatus(round.getStatus());
        response.setOutcome(round.getOutcome());

        response.setFeedback(round.getFeedback());
        response.setStrengths(round.getStrengths());
        response.setAreasForImprovement(round.getAreasForImprovement());
        response.setFeedbackSubmittedAt(round.getFeedbackSubmittedAt());

        response.setCancellationReason(round.getCancellationReason());
        response.setCancelledAt(round.getCancelledAt());

        response.setCreatedAt(round.getCreatedAt());
        response.setUpdatedAt(round.getUpdatedAt());

        return response;
    }

    public CandidateInterviewResponse toCandidateResponse(InterviewRound round) {

        CandidateInterviewResponse response = new CandidateInterviewResponse();

        response.setId(round.getId());
        response.setApplicationId(
                round.getInterviewProcess().getApplication().getId()
        );
        response.setRoundNumber(round.getRoundNumber());
        response.setTotalRounds(round.getInterviewProcess().getTotalRounds());
        response.setRoundName(round.getRoundName());

        response.setScheduledAt(round.getScheduledAt());
        response.setDurationMinutes(round.getDurationMinutes());

        response.setMode(round.getMode());
        response.setMeetingLink(round.getMeetingLink());
        response.setLocation(round.getLocation());

        response.setInterviewerId(round.getInterviewer().getId());
        response.setInterviewerName(
                round.getInterviewer().getFullName()
        );

        response.setStatus(round.getStatus());
        response.setOutcome(round.getOutcome());

        response.setCancellationReason(round.getCancellationReason());
        response.setCancelledAt(round.getCancelledAt());

        response.setCreatedAt(round.getCreatedAt());
        response.setUpdatedAt(round.getUpdatedAt());

        return response;
    }
}
