package com.example.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.entity.LoanApplication;

public interface LoanApplicationRepository
        extends JpaRepository<LoanApplication, Long> {
}