package com.HackPro.MedVault.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AadhaarVerificationService {

    public boolean verifyOTP(String aadhaarNumber, String otp) {
        // TODO: Integrate with UIDAI Aadhaar verification API
        log.info("Verifying Aadhaar OTP for: {}", maskAadhaar(aadhaarNumber));

        // Mock implementation
        return otp != null && otp.length() == 6;
    }

    private String maskAadhaar(String aadhaarNumber) {
        if (aadhaarNumber == null || aadhaarNumber.length() < 8) {
            return "****";
        }
        return "****" + aadhaarNumber.substring(aadhaarNumber.length() - 4);
    }
}
