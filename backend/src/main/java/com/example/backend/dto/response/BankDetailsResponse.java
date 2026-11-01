package com.example.backend.dto.response;

public class BankDetailsResponse {

    private Long bankDetailsId;

    private Long applicationId;

    private String accountHolderName;

    // Masked account number
    private String maskedAccountNumber;

    private String ifscCode;

    private String bankName;


    public BankDetailsResponse(
            Long bankDetailsId,
            Long applicationId,
            String accountHolderName,
            String maskedAccountNumber,
            String ifscCode,
            String bankName) {

        this.bankDetailsId = bankDetailsId;
        this.applicationId = applicationId;
        this.accountHolderName = accountHolderName;
        this.maskedAccountNumber = maskedAccountNumber;
        this.ifscCode = ifscCode;
        this.bankName = bankName;
    }


    public Long getBankDetailsId() {
        return bankDetailsId;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public String getMaskedAccountNumber() {
        return maskedAccountNumber;
    }

    public String getIfscCode() {
        return ifscCode;
    }

    public String getBankName() {
        return bankName;
    }
}