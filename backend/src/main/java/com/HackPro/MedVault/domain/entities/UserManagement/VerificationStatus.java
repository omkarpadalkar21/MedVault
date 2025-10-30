package com.HackPro.MedVault.domain.entities.UserManagement;

public enum VerificationStatus {
    PENDING,              // Initial state after doctor registration
    UNDER_REVIEW,         // Admin/system is actively verifying credentials
    VERIFIED,             // Successfully verified and approved
    REJECTED,             // Verification failed or credentials invalid
    SUSPENDED,            // Temporarily suspended due to investigation
    EXPIRED,              // License expired, needs renewal
    REVOKED,              // License revoked by medical board
    RESUBMISSION_REQUIRED // Additional documents or information needed
}

