package com.example.backend.service;

import com.example.backend.dto.request.KycEligibilityRequest;
import com.example.backend.dto.response.EligibilityResponse;
import com.example.backend.entity.DrivingLicense;
import com.example.backend.entity.LoanApplication;
import com.example.backend.enums.ApplicationStage;
import com.example.backend.enums.EligibilityStatus;
import com.example.backend.repository.DrivingLicenseRepository;
import com.example.backend.repository.LoanApplicationRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;

@Service
@Transactional
public class EligibilityService {

    private final LoanApplicationRepository applicationRepository;
    private final DrivingLicenseRepository dlRepository;

    public EligibilityService(
            LoanApplicationRepository appRepo,
            DrivingLicenseRepository dlRepo) {

        this.applicationRepository = appRepo;
        this.dlRepository = dlRepo;
    }

    public EligibilityResponse checkEligibility(
            Long applicationId,
            KycEligibilityRequest request) {

        // ------------------------------------------------
        // 1. Find Application
        // ------------------------------------------------

        LoanApplication app =
                applicationRepository.findById(applicationId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Application not found"));

        LocalDate today = LocalDate.now();


        // ------------------------------------------------
        // 2. Validate Licence Dates
        // ------------------------------------------------

        // Issue date cannot be in the future
        if (request.getIssueDate().isAfter(today)) {

            throw new IllegalArgumentException(
                    "Driving licence issue date cannot be in the future");
        }

        // Expiry date should be after issue date
        if (!request.getExpiryDate()
                .isAfter(request.getIssueDate())) {

            throw new IllegalArgumentException(
                    "Driving licence expiry date must be after issue date");
        }


        // ------------------------------------------------
        // 3. Save Driving Licence Details
        // ------------------------------------------------

        DrivingLicense dl = new DrivingLicense();

        dl.setLoanApplication(app);
        dl.setLicenseNumber(request.getLicenseNumber());
        dl.setIssueDate(request.getIssueDate());
        dl.setExpiryDate(request.getExpiryDate());
        dl.setDateOfBirth(request.getDateOfBirth());

        dlRepository.save(dl);


        // =================================================
        // 4. AGE CHECK
        // Rule: Age must be between 21 and 60
        // =================================================

        int age = Period.between(
                request.getDateOfBirth(),
                today
        ).getYears();

        boolean agePassed =
                age >= 21 && age <= 60;


        // =================================================
        // 5. LOAN-TO-VALUE (LTV) CHECK
        // Rule: Loan amount <= 80% of vehicle price
        // =================================================

        BigDecimal loanAmount =
                app.getLoan().getLoanAmount();

        BigDecimal vehiclePrice =
                app.getVehicle().getVehiclePrice();


        // Maximum loan allowed = 80% of vehicle price
        BigDecimal maxEligibleLoanAmount =
                vehiclePrice.multiply(
                        new BigDecimal("0.80")
                ).setScale(2, RoundingMode.HALF_UP);


        // Actual LTV percentage
        //
        // Example:
        // Loan = 280000
        // Vehicle = 350000
        //
        // LTV = 280000 / 350000 * 100
        //     = 80%
        BigDecimal ltvPercentage =
                loanAmount
                        .divide(
                                vehiclePrice,
                                4,
                                RoundingMode.HALF_UP
                        )
                        .multiply(new BigDecimal("100"))
                        .setScale(2, RoundingMode.HALF_UP);


        boolean ltvPassed =
                loanAmount.compareTo(
                        maxEligibleLoanAmount
                ) <= 0;


        // =================================================
        // 6. DRIVING EXPERIENCE CHECK
        // Rule: Minimum 1 year experience
        // =================================================

        int experienceYears = 0;

        boolean issueDateValid =
                !request.getIssueDate().isAfter(today);

        if (issueDateValid) {

            experienceYears =
                    Period.between(
                            request.getIssueDate(),
                            today
                    ).getYears();
        }

        boolean experiencePassed =
                issueDateValid
                && experienceYears >= 1;


        // =================================================
        // 7. LICENCE VALIDITY CHECK
        //
        // Rule:
        // Licence must be valid for MORE THAN 1 month
        // from today's date.
        // =================================================

        LocalDate minimumValidUntil =
                today.plusMonths(1);


        boolean validityPassed =
                request.getExpiryDate()
                        .isAfter(minimumValidUntil);


        // Actual remaining validity in months
        //
        // Example:
        // Today       = 2026-10-04
        // Expiry date = 2028-01-10
        //
        // Remaining = around 15 months
        int licenseValidityRemainingMonths =
                Period.between(
                        today,
                        request.getExpiryDate()
                ).getMonths();


        // =================================================
        // 8. Create Response
        // =================================================

        EligibilityResponse response =
                new EligibilityResponse();


        // ----- Age -----

        response.setAge(age);
        response.setAgeCheckPassed(agePassed);


        // ----- Experience -----

        response.setExperienceYears(
                experienceYears
        );

        response.setExperienceCheckPassed(
                experiencePassed
        );


        // ----- LTV -----

        response.setLoanAmount(loanAmount);

        response.setVehiclePrice(vehiclePrice);

        response.setMaxEligibleLoanAmount(
                maxEligibleLoanAmount
        );

        response.setLtvPercentage(
                ltvPercentage
        );

        response.setLtvCheckPassed(
                ltvPassed
        );


        // ----- Licence Validity -----

        response.setLicenseExpiryDate(
                request.getExpiryDate()
        );

        response.setLicenseValidityRemainingMonths(
                licenseValidityRemainingMonths
        );

        response.setValidityCheckPassed(
                validityPassed
        );


        // =================================================
        // 9. Overall Eligibility
        // =================================================

        boolean eligible =
                agePassed
                && ltvPassed
                && experiencePassed
                && validityPassed;


        if (eligible) {

            response.setOverallStatus(
                    EligibilityStatus.ELIGIBLE.name()
            );

            response.setMessage(
                    "Customer is eligible for the loan."
            );

            // All checks passed.
            // Move application to Review & Consent.
            app.setCurrentStage(
                    ApplicationStage.REVIEW_AND_CONSENT
            );

        } else {

            response.setOverallStatus(
                    EligibilityStatus.INELIGIBLE.name()
            );

            response.setMessage(
                    "Customer does not meet the eligibility criteria."
            );

            // One or more checks failed.
            // Stay in KYC & Eligibility.
            app.setCurrentStage(
                    ApplicationStage.KYC_AND_ELIGIBILITY
            );
        }


        // Save updated application stage
        applicationRepository.save(app);

        return response;
    }
}