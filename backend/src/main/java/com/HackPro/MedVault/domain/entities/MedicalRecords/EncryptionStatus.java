package com.HackPro.MedVault.domain.entities.MedicalRecords;

public enum EncryptionStatus {
    UNENCRYPTED,              // Data not yet encrypted (initial upload state)
    ENCRYPTING,               // Encryption process in progress
    ENCRYPTED,                // Successfully encrypted and secure
    ENCRYPTION_FAILED,        // Encryption attempt failed
    PENDING_RE_ENCRYPTION,    // Needs re-encryption (key rotation, algorithm upgrade)
    PARTIALLY_ENCRYPTED,      // Some components encrypted, others pending
    DECRYPTED_TEMP,          // Temporarily decrypted for processing
    CORRUPTED,                // Encryption corrupted, needs attention
    KEY_ROTATION_REQUIRED     // Encryption key rotation needed
}

