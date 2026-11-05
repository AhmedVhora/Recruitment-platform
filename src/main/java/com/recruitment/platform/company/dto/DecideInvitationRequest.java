package com.recruitment.platform.company.dto;

import jakarta.validation.constraints.NotNull;

import com.recruitment.platform.company.entity.MembershipRequestStatus;

public class DecideInvitationRequest {

    @NotNull(message = "Request status is required")
    private MembershipRequestStatus requestStatus;

    public MembershipRequestStatus getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(MembershipRequestStatus requestStatus) {
        this.requestStatus = requestStatus;
    }
}