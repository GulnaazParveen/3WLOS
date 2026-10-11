package com.example.backend.controller;

import com.example.backend.dto.request.KycEligibilityRequest;
import com.example.backend.dto.response.EligibilityResponse;
import com.example.backend.enums.DocumentType;
import com.example.backend.service.DocumentService;
import com.example.backend.service.EligibilityService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/applications/{applicationId}/kyc")
public class KycEligibilityController {

    private final DocumentService documentService;
    private final EligibilityService eligibilityService;

    public KycEligibilityController(DocumentService docService, EligibilityService eligService) {
        this.documentService = docService;
        this.eligibilityService = eligService;
    }

    // Endpoint 1: Upload Aadhaar
  @PostMapping("/documents")
public ResponseEntity<String> uploadDocument(
        @PathVariable Long applicationId,
        @RequestParam("type") DocumentType type,
        @RequestParam("file") MultipartFile file) {

    try {
        documentService.uploadDocument(applicationId, type, file);
        return ResponseEntity.ok(type + " uploaded successfully");

    } catch (IOException e) {
        return ResponseEntity.internalServerError()
                .body("Failed to upload document");
    }
}
    // Endpoint 2: Submit DL & Check Eligibility
    @PostMapping("/check-eligibility")
    public ResponseEntity<EligibilityResponse> checkEligibility(
            @PathVariable Long applicationId,
            @Valid @RequestBody KycEligibilityRequest request) {

        EligibilityResponse response = eligibilityService.checkEligibility(applicationId, request);
        return ResponseEntity.ok(response);
    }
}