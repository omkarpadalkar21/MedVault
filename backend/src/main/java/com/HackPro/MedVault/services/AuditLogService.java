package com.HackPro.MedVault.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class AuditLogService {

    public void logAuthenticationEvent(UUID userId, String eventType, String ipAddress) {
        log.info("Authentication Event - User: {}, Type: {}, IP: {}", userId, eventType, ipAddress);
        // TODO: Save to database
    }

    public void logAuthenticationEvent(UUID userId, String eventType, String ipAddress, Map<String, String> metadata) {
        log.info("Authentication Event - User: {}, Type: {}, IP: {}, Metadata: {}",
                userId, eventType, ipAddress, metadata);
        // TODO: Save to database
    }

    public void logAccessEvent(UUID doctorId, UUID patientId, String actionType, String description) {
        log.info("Access Event - Doctor: {}, Patient: {}, Action: {}, Description: {}",
                doctorId, patientId, actionType, description);
        // TODO: Save to database
    }

    public void logSecurityEvent(String eventType, String ipAddress, String details) {
        log.warn("Security Event - Type: {}, IP: {}, Details: {}", eventType, ipAddress, details);
        // TODO: Save to database and trigger alerts
    }
}
