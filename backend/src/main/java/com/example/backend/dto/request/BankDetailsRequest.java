package com.example.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class BankDetailsRequest {

    // Customer's name as registered in the bank account
    @NotBlank(message = "Account holder name is required")
    private String accountHolderName;

    // Bank account number
    @NotBlank(message = "Account number is required")
    private String accountNumber;

    /*
     * Indian IFSC format:
     *
     * 4 letters
     * followed by 0
     * followed by 6 alphanumeric characters
     *
     * Example:
     * SBIN0001234
     */
    @NotBlank(message = "IFSC code is required")
    @Pattern(
        regexp = "^[A-Z]{4}0[A-Z0-9]{6}$",
        message = "Invalid IFSC code"
    )
    private String ifscCode;

    // Bank name
    @NotBlank(message = "Bank name is required")
    private String bankName;


    // Getters and setters

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public void setAccountHolderName(String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getIfscCode() {
        return ifscCode;
    }

    public void setIfscCode(String ifscCode) {
        this.ifscCode = ifscCode;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
}
