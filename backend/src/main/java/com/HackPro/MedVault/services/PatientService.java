package com.HackPro.MedVault.services;

import com.HackPro.MedVault.domain.dtos.PatientResponseDto;

import java.util.UUID;

public interface PatientService {

    /**
     * Get patient profile by user ID
     *
     * @param userId Patient's user ID
     * @return PatientResponseDto
     */
    PatientResponseDto getPatientProfile(UUID userId);
}
