package com.example.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.entity.Loan;

public interface LoanRepository
        extends JpaRepository<Loan, Long> {
}
