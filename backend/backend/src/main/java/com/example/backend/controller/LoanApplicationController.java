package com.example.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.request.CreateApplicationRequest;
import com.example.backend.dto.response.CreateApplicationResponse;
import com.example.backend.service.LoanApplicationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/applications")
public class LoanApplicationController {

    private final LoanApplicationService service;

    public LoanApplicationController(
            LoanApplicationService service) {

        this.service = service;
    }

    // Module 1: Create application
    @PostMapping
    public ResponseEntity<CreateApplicationResponse>
    createApplication(
            @Valid @RequestBody
            CreateApplicationRequest request) {

        CreateApplicationResponse response =
                service.createApplication(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    // Module 1 completed → move to Module 2
    @PostMapping("/{applicationId}/submit")
    public ResponseEntity<String> submitApplication(
            @PathVariable Long applicationId) {

        service.submitApplication(applicationId);

        return ResponseEntity.ok(
                "Application submitted successfully. "
                + "Moved to KYC and Eligibility."
        );
    }
}