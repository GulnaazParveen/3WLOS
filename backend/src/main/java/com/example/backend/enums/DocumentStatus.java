package com.example.backend.enums;

public enum DocumentStatus {
    PENDING,
    UPLOADED,
    VERIFIED,
    REJECTED,
    // New status for system-generated PDFs
    GENERATED
}