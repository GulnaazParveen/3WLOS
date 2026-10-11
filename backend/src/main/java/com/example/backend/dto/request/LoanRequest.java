package com.example.backend.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class LoanRequest {

    @NotNull
    @DecimalMin("1.00")
    private BigDecimal loanAmount;

    @NotNull
    @Min(6)
    @Max(84)
    private Integer tenureMonths;

    // Getters and Setters

    public BigDecimal getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(BigDecimal loanAmount) {
        this.loanAmount = loanAmount;
    }

    public Integer getTenureMonths() {
        return tenureMonths;
    }

    public void setTenureMonths(Integer tenureMonths) {
        this.tenureMonths = tenureMonths;
    }
}