package com.HackPro.MedVault.domain.entities.MedicalRecords;

public enum ProcessingStatus {
    PENDING,      // File uploaded, waiting for n8n processing
    PROCESSING,   // n8n is currently processing
    COMPLETED,    // Successfully processed
    FAILED        // Processing failed
}
