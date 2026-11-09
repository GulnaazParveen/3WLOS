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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final LoanApplicationRepository applicationRepository;
  private final PdfGenerationService pdfGenerationService;
    
    // Local folder jahan files save hongi (Demo ke liye)
    private final String UPLOAD_DIR = "C:/uploads/loan_documents/"; 

    public DocumentService(DocumentRepository docRepo, LoanApplicationRepository appRepo,PdfGenerationService pdfGenerationService) {
        this.documentRepository = docRepo;
        this.applicationRepository = appRepo;
        this.pdfGenerationService = pdfGenerationService;
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

     // ============================================================
    // MODULE 4 - GENERATE KFS AND AGREEMENT
    // ============================================================

    public List<Document> generateLoanDocuments(
            Long applicationId) throws IOException {

        // --------------------------------------------------------
        // 1. Find the application
        // --------------------------------------------------------

        LoanApplication app = applicationRepository.findById(applicationId)
                .orElseThrow(() ->
                        new RuntimeException("Application not found"));

        // List that will contain KFS and Agreement documents.
        List<Document> documents = new ArrayList<>();


        // --------------------------------------------------------
        // 2. Generate / get KFS
        // --------------------------------------------------------

        Optional<Document> existingKfs =
                documentRepository.findByLoanApplicationAndDocumentType(
                        app,
                        DocumentType.KFS
                );

        if (existingKfs.isPresent()) {

            // KFS already exists.
            // We don't generate another PDF.
            documents.add(existingKfs.get());

        } else {

            // KFS does not exist.
            // Generate a new KFS PDF.
            File kfsFile =
                    pdfGenerationService.generateKfsPdf(app);

            // Create Document database record.
            Document kfsDocument = new Document();

            kfsDocument.setLoanApplication(app);
            kfsDocument.setDocumentType(DocumentType.KFS);
            kfsDocument.setFileName(kfsFile.getName());
            kfsDocument.setFilePath(kfsFile.getAbsolutePath());
            kfsDocument.setStatus(DocumentStatus.GENERATED);
            kfsDocument.setUploadedAt(LocalDateTime.now());

            // Save KFS metadata in document table.
            Document savedKfs =
                    documentRepository.save(kfsDocument);

            documents.add(savedKfs);
        }


        // --------------------------------------------------------
        // 3. Generate / get Agreement
        // --------------------------------------------------------

        Optional<Document> existingAgreement =
                documentRepository.findByLoanApplicationAndDocumentType(
                        app,
                        DocumentType.AGREEMENT
                );

        if (existingAgreement.isPresent()) {

            // Agreement already exists.
            // Don't create another PDF.
            documents.add(existingAgreement.get());

        } else {

            // Agreement does not exist.
            // Generate a new Agreement PDF.
            File agreementFile =
                    pdfGenerationService.generateAgreementPdf(app);

            // Create Document database record.
            Document agreementDocument = new Document();

            agreementDocument.setLoanApplication(app);
            agreementDocument.setDocumentType(
                    DocumentType.AGREEMENT
            );
            agreementDocument.setFileName(
                    agreementFile.getName()
            );
            agreementDocument.setFilePath(
                    agreementFile.getAbsolutePath()
            );
            agreementDocument.setStatus(
                    DocumentStatus.GENERATED
            );
            agreementDocument.setUploadedAt(
                    LocalDateTime.now()
            );

            // Save Agreement metadata in database.
            Document savedAgreement =
                    documentRepository.save(agreementDocument);

            documents.add(savedAgreement);
        }


        // Return both documents.
        return documents;
    }


// ============================================================
// MODULE 4 - GET APPLICATION DOCUMENTS
// ============================================================

public List<Document> getApplicationDocuments(Long applicationId) {

    // 1. Check whether the loan application exists.
    applicationRepository.findById(applicationId)
            .orElseThrow(() ->
                    new RuntimeException("Application not found"));

    // 2. Fetch all documents linked to this application.
    // This can return KFS, Agreement, Aadhaar, PAN, etc.
    return documentRepository
            .findByLoanApplication_ApplicationId(applicationId);
}


// ============================================================
// STEP 3 - GET PDF FILE FOR VIEW / DOWNLOAD
// ============================================================

public File getDocumentFile(Long applicationId, Long documentId) {

    // 1. Check whether the application exists.
    applicationRepository.findById(applicationId)
            .orElseThrow(() ->
                    new RuntimeException("Application not found"));

    // 2. Find the document using documentId.
    Document document = documentRepository.findById(documentId)
            .orElseThrow(() ->
                    new RuntimeException("Document not found"));

    // 3. Check whether this document belongs to this application.
    if (!document.getLoanApplication()
            .getApplicationId().equals(applicationId)) {

        throw new RuntimeException(
                "Document does not belong to this application");
    }

    // 4. Get the actual PDF file from the saved file path.
    File file = new File(document.getFilePath());

    // 5. Check whether the file exists.
    if (!file.exists() || !file.isFile()) {
        throw new RuntimeException("PDF file not found");
    }

    // 6. Return the PDF file.
    return file;
}


}
