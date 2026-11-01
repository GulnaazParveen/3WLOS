package com.example.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.dto.request.BankDetailsRequest;
import com.example.backend.dto.response.BankDetailsResponse;
import com.example.backend.entity.BankDetails;
import com.example.backend.entity.LoanApplication;
import com.example.backend.enums.ApplicationStage;
import com.example.backend.repository.BankDetailsRepository;
import com.example.backend.repository.LoanApplicationRepository;

@Service
@Transactional
public class BankDetailsService {

    private final LoanApplicationRepository applicationRepository;

    private final BankDetailsRepository bankDetailsRepository;


    public BankDetailsService(
            LoanApplicationRepository applicationRepository,
            BankDetailsRepository bankDetailsRepository) {

        this.applicationRepository = applicationRepository;
        this.bankDetailsRepository = bankDetailsRepository;
    }


    public BankDetailsResponse saveBankDetails(
            Long applicationId,
            BankDetailsRequest request) {

        /*
         * Step 1:
         * Find the loan application.
         */
        LoanApplication application =
                applicationRepository.findById(applicationId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Application not found"));


        /*
         * Step 2:
         * Bank details are allowed only when the application
         * has reached Review & Consent.
         *
         * KYC eligibility should already be completed.
         */
        if (application.getCurrentStage()
                != ApplicationStage.REVIEW_AND_CONSENT) {

            throw new IllegalStateException(
                    "Bank details can be added only during Review and Consent stage");
        }


        /*
         * Step 3:
         * Check whether bank details already exist.
         *
         * If they exist, update them.
         * Otherwise, create a new record.
         *
         * This is important because the frontend may allow
         * the customer/user to edit bank details.
         */
        BankDetails bankDetails =
                bankDetailsRepository
                        .findByLoanApplication(application)
                        .orElseGet(() -> {

                            BankDetails newBankDetails =
                                    new BankDetails();

                            newBankDetails
                                    .setLoanApplication(application);

                            return newBankDetails;
                        });


        /*
         * Step 4:
         * Copy request data into entity.
         */
        bankDetails.setAccountHolderName(
                request.getAccountHolderName());

        bankDetails.setAccountNumber(
                request.getAccountNumber());

        /*
         * Store IFSC in uppercase.
         *
         * Example:
         * sbin0001234
         * becomes
         * SBIN0001234
         */
        bankDetails.setIfscCode(
                request.getIfscCode().toUpperCase());

        bankDetails.setBankName(
                request.getBankName());


        /*
         * Step 5:
         * Save bank details.
         */
        BankDetails saved =
                bankDetailsRepository.save(bankDetails);


        /*
         * Step 6:
         * Return a safe response.
         *
         * We do NOT return the complete account number.
         */
        return mapToResponse(saved);
    }


    /*
     * Convert BankDetails entity into API response.
     */
    private BankDetailsResponse mapToResponse(
            BankDetails bankDetails) {

        String accountNumber =
                bankDetails.getAccountNumber();

        String maskedAccountNumber =
                maskAccountNumber(accountNumber);


        return new BankDetailsResponse(
                bankDetails.getId(),
                bankDetails.getLoanApplication()
                        .getApplicationId(),
                bankDetails.getAccountHolderName(),
                maskedAccountNumber,
                bankDetails.getIfscCode(),
                bankDetails.getBankName()
        );
    }


    /*
     * Example:
     *
     * 123456789012
     *       ↓
     * ********9012
     *
     * Only the last four digits are shown.
     */
    private String maskAccountNumber(
            String accountNumber) {

        if (accountNumber == null ||
                accountNumber.length() <= 4) {

            return "****";
        }

        int visibleDigits = 4;

        String lastFour =
                accountNumber.substring(
                        accountNumber.length() - visibleDigits);

        return "******" + lastFour;
    }
}