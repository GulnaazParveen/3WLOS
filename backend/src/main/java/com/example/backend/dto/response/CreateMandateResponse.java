
package com.example.backend.dto.response;

import com.example.backend.enums.MandateStatus;

public class CreateMandateResponse {

    private Long mandateId;
    private Long applicationId;
    private String mandateReference;
    private MandateStatus status;
    private String customerActionLink;
    private String message;

    // Constructor used by MandateService.
    public CreateMandateResponse(
            Long mandateId,
            Long applicationId,
            String mandateReference,
            MandateStatus status,
            String customerActionLink,
            String message) {

        this.mandateId = mandateId;
        this.applicationId = applicationId;
        this.mandateReference = mandateReference;
        this.status = status;
        this.customerActionLink = customerActionLink;
        this.message = message;
    }

    public Long getMandateId() {
        return mandateId;
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

    public String getCustomerActionLink() {
        return customerActionLink;
    }

    public String getMessage() {
        return message;
    }
}
