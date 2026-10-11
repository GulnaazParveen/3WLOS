package com.example.backend.service;

import com.example.backend.entity.Document;
import com.example.backend.entity.LoanApplication;
import com.example.backend.enums.DocumentStatus;
import com.example.backend.enums.DocumentType;
import com.example.backend.repository.DocumentRepository;
import com.example.backend.repository.LoanApplicationRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final LoanApplicationRepository applicationRepository;
    
    // Local folder jahan files save hongi (Demo ke liye)
    private final String UPLOAD_DIR = "C:/uploads/loan_documents/"; 

    public DocumentService(DocumentRepository docRepo, LoanApplicationRepository appRepo) {
        this.documentRepository = docRepo;
        this.applicationRepository = appRepo;
    }

    public Document uploadDocument(Long applicationId, DocumentType type, MultipartFile file) throws IOException {
        LoanApplication app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        // 1. Create Directory if not exists
        File directory = new File(UPLOAD_DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // 2. Save file locally
        String filePath = UPLOAD_DIR + System.currentTimeMillis() + "_" + file.getOriginalFilename();
        file.transferTo(new File(filePath));

        // 3. Save to Database
        Document document = new Document();
        document.setLoanApplication(app);
        document.setDocumentType(type);
        document.setFileName(file.getOriginalFilename());
        document.setFilePath(filePath);
        document.setStatus(DocumentStatus.UPLOADED);
        document.setUploadedAt(LocalDateTime.now());

        return documentRepository.save(document);
    }
}
