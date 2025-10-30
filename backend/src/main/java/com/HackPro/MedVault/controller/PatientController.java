package com.HackPro.MedVault.controller;

import com.HackPro.MedVault.domain.dtos.PatientResponseDto;
import com.HackPro.MedVault.security.MedVaultUserDetails;
import com.HackPro.MedVault.services.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    /**
     * Get current authenticated patient's profile
     *
     * @param userDetails Authenticated user details from JWT token
     * @return PatientResponseDto with patient information
     */
    @GetMapping("/profile")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<PatientResponseDto> getPatientProfile(
            @AuthenticationPrincipal MedVaultUserDetails userDetails) {

        PatientResponseDto profile = patientService.getPatientProfile(userDetails.getUserId());
        return ResponseEntity.ok(profile);
    }

    /**
     * Get patient profile by ID (Admin only)
     *
     * @param patientId Patient UUID
     * @return PatientResponseDto
     */
    @GetMapping("/{patientId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PatientResponseDto> getPatientById(@PathVariable UUID patientId) {
        PatientResponseDto profile = patientService.getPatientProfile(patientId);
        return ResponseEntity.ok(profile);
    }
}
