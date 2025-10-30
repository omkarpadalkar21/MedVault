package com.HackPro.MedVault.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class MFAService {

    private final Map<UUID, MFAVerification> mfaCache = new ConcurrentHashMap<>();
    private final SecureRandom secureRandom = new SecureRandom();

    public String generateOTP() {
        int otp = 100000 + secureRandom.nextInt(900000);
        return String.valueOf(otp);
    }

    public void sendMFACode(UUID userId, String email) {
        String code = generateOTP();
        MFAVerification verification = new MFAVerification(code, LocalDateTime.now().plusMinutes(10));
        mfaCache.put(userId, verification);

        log.info("MFA code generated for user: {} - Code: {}", email, code);
        // TODO: Integrate with SMS/Email service
    }

    public boolean verifyMFACode(UUID userId, String code) {
        MFAVerification verification = mfaCache.get(userId);

        if (verification == null) {
            log.warn("No MFA verification found for user: {}", userId);
            return false;
        }

        if (LocalDateTime.now().isAfter(verification.expiryTime)) {
            log.warn("MFA code expired for user: {}", userId);
            mfaCache.remove(userId);
            return false;
        }

        boolean isValid = verification.code.equals(code);
        if (isValid) {
            mfaCache.remove(userId);
        }

        return isValid;
    }

    public boolean hasRecentMFAVerification(UUID userId) {
        // Check if user verified MFA in last 30 minutes for sensitive operations
        // TODO: Implement with database/cache
        return true;
    }

    private record MFAVerification(String code, LocalDateTime expiryTime) {}
}
