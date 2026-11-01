
package com.example.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class VerifyMandateOtpRequest {

    @NotBlank(message = "OTP is required")
    @Pattern(
        regexp = "^[0-9]{6}$",
        message = "OTP must contain exactly 6 digits"
    )
    private String otp;

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
