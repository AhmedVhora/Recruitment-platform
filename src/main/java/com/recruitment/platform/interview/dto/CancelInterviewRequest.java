
package com.recruitment.platform.interview.dto;

import jakarta.validation.constraints.Size;

public class CancelInterviewRequest {

    @Size(max = 1000)
    private String cancellationReason;

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }
}
