package com.example.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(
    name = "bank_details",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "application_id")
    }
)
public class BankDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * One loan application can have only one bank-details record.
     */
    @OneToOne
    @JoinColumn(
        name = "application_id",
        nullable = false,
        unique = true
    )
    private LoanApplication loanApplication;

    // Account holder name
    @Column(nullable = false)
    private String accountHolderName;

    // Bank account number
    @Column(nullable = false)
    private String accountNumber;

    // IFSC code
    @Column(nullable = false)
    private String ifscCode;

    // Bank name
    @Column(nullable = false)
    private String bankName;

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LoanApplication getLoanApplication() {
        return loanApplication;
    }

    public void setLoanApplication(LoanApplication loanApplication) {
        this.loanApplication = loanApplication;
    }

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