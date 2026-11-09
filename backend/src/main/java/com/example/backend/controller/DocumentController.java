
package com.example.backend.controller;

import com.example.backend.entity.Document;
import com.example.backend.service.DocumentService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.io.File;

@RestController
@RequestMapping("/api/v1/applications/{applicationId}/documents")
public class DocumentController {

    private final DocumentService documentService;

    // Constructor injection: Spring Boot provides DocumentService.
    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    // MODULE 4: Generate KFS and Agreement PDFs.
    @PostMapping("/generate")
    public ResponseEntity<List<Document>> generateLoanDocuments(
            @PathVariable Long applicationId) throws IOException {

        // Send applicationId to the service.
        // Service generates both PDFs and saves their records.
        List<Document> documents = documentService.generateLoanDocuments(applicationId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(documents);
    }

    // GET all documents for an application.
    @GetMapping
    public ResponseEntity<List<Document>> getApplicationDocuments(
            @PathVariable Long applicationId) {

        List<Document> documents = documentService.getApplicationDocuments(applicationId);

        return ResponseEntity.ok(documents);
    }


 // ============================================================
 // STEP 3 - VIEW PDF
 // ============================================================

 @GetMapping("/{documentId}/view")
 public ResponseEntity<Resource> viewDocument(
         @PathVariable Long applicationId,
         @PathVariable Long documentId) {

     // Get the PDF file from DocumentService.
     File file = documentService.getDocumentFile(
             applicationId, documentId);

     // Convert the file into a Resource.
     Resource resource = new FileSystemResource(file);

     // Return PDF so browser can open it.
     return ResponseEntity.ok()
             .contentType(MediaType.APPLICATION_PDF)
             .header(HttpHeaders.CONTENT_DISPOSITION,
                     "inline; filename=\"" + file.getName() + "\"")
             .body(resource);
 }


 // ============================================================
 // STEP 3 - DOWNLOAD PDF
 // ============================================================

 @GetMapping("/{documentId}/download")
 public ResponseEntity<Resource> downloadDocument(
         @PathVariable Long applicationId,
         @PathVariable Long documentId) {

     // Get the PDF file from DocumentService.
     File file = documentService.getDocumentFile(
             applicationId, documentId);

     // Convert the file into a Resource.
     Resource resource = new FileSystemResource(file);

     // Return PDF as a downloadable attachment.
     return ResponseEntity.ok()
             .contentType(MediaType.APPLICATION_PDF)
             .header(HttpHeaders.CONTENT_DISPOSITION,
                     "attachment; filename=\"" + file.getName() + "\"")
             .body(resource);
 }


}
