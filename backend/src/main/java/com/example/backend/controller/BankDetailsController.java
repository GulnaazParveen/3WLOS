package com.example.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import com.example.backend.dto.request.BankDetailsRequest;
import com.example.backend.dto.response.BankDetailsResponse;
import com.example.backend.service.BankDetailsService;

@RestController
@RequestMapping("/api/v1/applications/{applicationId}/bank-details")
public class BankDetailsController {

    private final BankDetailsService bankDetailsService;


    public BankDetailsController(
            BankDetailsService bankDetailsService) {

        this.bankDetailsService = bankDetailsService;
    }


    /*
     * Save or update bank details for an application.
     */
    @PostMapping
    public ResponseEntity<BankDetailsResponse> saveBankDetails(

            @PathVariable Long applicationId,

            @Valid @RequestBody
            BankDetailsRequest request) {

        BankDetailsResponse response =
                bankDetailsService.saveBankDetails(
                        applicationId,
                        request);

        return ResponseEntity.ok(response);
    }
}
