
package com.example.backend.dto.response;

import com.example.backend.enums.MandateStatus;

public class MandateStatusResponse {

    private Long applicationId;
    private String mandateReference;
    private MandateStatus status;
    private String message;

    // Constructor used by MandateService.
    public MandateStatusResponse(
            Long applicationId,
            String mandateReference,
            MandateStatus status,
            String message) {

        this.applicationId = applicationId;
        this.mandateReference = mandateReference;
        this.status = status;
        this.message = message;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public String getMandateReference() {
        return mandateReference;
    }

    public MandateStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}

