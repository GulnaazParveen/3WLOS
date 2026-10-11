package com.example.backend.repository;

import com.example.backend.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByLoanApplication_ApplicationId(Long applicationId);
}