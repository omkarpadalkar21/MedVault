package com.HackPro.MedVault.services.impl;

import com.HackPro.MedVault.domain.dtos.PatientResponseDto;
import com.HackPro.MedVault.domain.entities.UserManagement.Patient;
import com.HackPro.MedVault.exceptions.ResourceNotFoundException;
import com.HackPro.MedVault.repositories.PatientRepository;
import com.HackPro.MedVault.services.EncryptionService;
import com.HackPro.MedVault.services.PatientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final EncryptionService encryptionService;

    /**
     * Get patient profile by user ID
     * Aadhaar number is NOT returned for security reasons
     * Handles existing users without timestamps gracefully
     *
     * @param userId Patient's user ID
     * @return PatientResponseDto with patient information
     * @throws ResourceNotFoundException if patient not found
     */
    @Override
    @Transactional(readOnly = true)
    public PatientResponseDto getPatientProfile(UUID userId) {
        log.info("Fetching patient profile for user ID: {}", userId);

        // Find patient by ID
        Patient patient = patientRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("Patient not found with ID: {}", userId);
                    return new ResourceNotFoundException("Patient not found");
                });

        log.debug("Patient profile found for: {}", patient.getEmail());

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime createdAt = patient.getCreatedAt() != null ? patient.getCreatedAt() : now;
        LocalDateTime updatedAt = patient.getUpdatedAt() != null ? patient.getUpdatedAt() : now;

        return PatientResponseDto.builder()
                .id(patient.getId())
                .email(patient.getEmail())
                .phoneNumber(patient.getPhoneNumber())
                .isActive(patient.getIsActive())
                .mfaEnabled(patient.getMfaEnabled())
                .firstName(patient.getFirstName())
                .lastName(patient.getLastName())
                .dateOfBirth(patient.getDateOfBirth())
                .gender(patient.getGender())
                .bloodGroup(patient.getBloodGroup())
                .address(patient.getAddress())
                .allergies(patient.getAllergies())
                .chronicConditions(patient.getChronicConditions())
                .emergencyContactName(patient.getEmergencyContactName())
                .emergencyContactPhone(patient.getEmergencyContactPhone())
                .createdAt(createdAt)  // From User parent
                .updatedAt(updatedAt)  // From User parent
                .build();
    }

    /**
     * Get patient profile by Aadhaar number
     * Encrypts the plain Aadhaar number and searches in database
     *
     * @param aadhaarNumber Patient's Aadhaar number (plain text)
     * @return PatientResponseDto with patient information
     * @throws ResourceNotFoundException if patient not found
     */
    @Override
    @Transactional(readOnly = true)
    public PatientResponseDto getPatientByAadhaar(String aadhaarNumber) {
        log.info("Searching patient by Aadhaar number");

        // Encrypt the Aadhaar number to match stored format
        String encryptedAadhaar = encryptionService.encrypt(aadhaarNumber);

        // Find patient by encrypted Aadhaar
        Patient patient = patientRepository.findByAadhaarNumber(encryptedAadhaar)
                .orElseThrow(() -> {
                    log.error("Patient not found with provided Aadhaar number");
                    return new ResourceNotFoundException("Patient not found with provided Aadhaar number");
                });

        log.debug("Patient found: {}", patient.getEmail());

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime createdAt = patient.getCreatedAt() != null ? patient.getCreatedAt() : now;
        LocalDateTime updatedAt = patient.getUpdatedAt() != null ? patient.getUpdatedAt() : now;

        return PatientResponseDto.builder()
                .id(patient.getId())
                .email(patient.getEmail())
                .phoneNumber(patient.getPhoneNumber())
                .isActive(patient.getIsActive())
                .mfaEnabled(patient.getMfaEnabled())
                .firstName(patient.getFirstName())
                .lastName(patient.getLastName())
                .dateOfBirth(patient.getDateOfBirth())
                .gender(patient.getGender())
                .bloodGroup(patient.getBloodGroup())
                .address(patient.getAddress())
                .allergies(patient.getAllergies())
                .chronicConditions(patient.getChronicConditions())
                .emergencyContactName(patient.getEmergencyContactName())
                .emergencyContactPhone(patient.getEmergencyContactPhone())
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }
}
