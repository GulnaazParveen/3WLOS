package com.example.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.entity.BankDetails;
import com.example.backend.entity.LoanApplication;

public interface BankDetailsRepository
        extends JpaRepository<BankDetails, Long> {

    /*
     * Find bank details using the loan application.
     *
     * We use this because one application has one bank-details record.
     */
    Optional<BankDetails> findByLoanApplication(
            LoanApplication loanApplication);
}