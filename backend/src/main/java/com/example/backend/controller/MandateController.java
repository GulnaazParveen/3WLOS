
package com.example.backend.controller;

import com.example.backend.dto.request.VerifyMandateOtpRequest;
import com.example.backend.dto.response.CreateMandateResponse;
import com.example.backend.dto.response.MandateStatusResponse;
import com.example.backend.service.MandateService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class MandateController {

    private final MandateService mandateService;

    public MandateController(MandateService mandateService) {
        this.mandateService = mandateService;
    }

    // 1. LOS user initiates a new mandate.
    @PostMapping("/applications/{applicationId}/mandate")
    public ResponseEntity<CreateMandateResponse> createMandate(
            @PathVariable Long applicationId) {

        CreateMandateResponse response =
                mandateService.createMandate(applicationId);

        return ResponseEntity.ok(response);
    }

    // 2. Customer requests an OTP using the action link.
    @PostMapping("/mandates/{token}/send-otp")
    public ResponseEntity<MandateStatusResponse> sendOtp(
            @PathVariable String token) {

        MandateStatusResponse response =
                mandateService.sendOtp(token);

        return ResponseEntity.ok(response);
    }

    // 3. Customer submits the six-digit OTP.
    @PostMapping("/mandates/{token}/verify-otp")
    public ResponseEntity<MandateStatusResponse> verifyOtp(
            @PathVariable String token,
            @Valid @RequestBody VerifyMandateOtpRequest request) {

        MandateStatusResponse response =
                mandateService.verifyOtp(token, request.getOtp());

        return ResponseEntity.ok(response);
    }

    // 4. LOS user checks the latest mandate status.
    @GetMapping("/applications/{applicationId}/mandate/status")
    public ResponseEntity<MandateStatusResponse> getMandateStatus(
            @PathVariable Long applicationId) {

        MandateStatusResponse response =
                mandateService.getLatestMandateStatus(applicationId);

        return ResponseEntity.ok(response);
    }
}
