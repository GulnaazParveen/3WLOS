package com.example.backend.repository;

import com.example.backend.entity.Document;
import com.example.backend.entity.LoanApplication;
import com.example.backend.enums.DocumentType;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByLoanApplication_ApplicationId(Long applicationId);

    // Find a specific document type for a particular application
    Optional<Document> findByLoanApplicationAndDocumentType(
            LoanApplication loanApplication,
            DocumentType documentType
    );
}