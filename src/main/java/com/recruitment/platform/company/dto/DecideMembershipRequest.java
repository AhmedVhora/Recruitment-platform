package com.recruitment.platform.company.dto;

import com.recruitment.platform.company.entity.MembershipRequestStatus;
import jakarta.validation.constraints.NotNull;

public class DecideMembershipRequest {

    @NotNull(message = "Request status is required")
    private MembershipRequestStatus status;

    public DecideMembershipRequest() {
    }

    public DecideMembershipRequest(MembershipRequestStatus status) {
        this.status = status;
    }

    public MembershipRequestStatus getStatus() {
        return status;
    }

    public void setStatus(MembershipRequestStatus status) {
        this.status = status;
    }
}