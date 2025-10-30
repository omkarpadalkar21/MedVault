package com.HackPro.MedVault.domain.dtos;

import com.HackPro.MedVault.domain.entities.UserManagement.Gender;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientResponseDto {

    // User base information
    private UUID id;
    private String email;
    private String phoneNumber;
    private Boolean isActive;
    private Boolean mfaEnabled;

    // Patient personal information
    private String firstName;
    private String lastName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dateOfBirth;

    private Gender gender;
    private String bloodGroup;
    private String address;

    // Medical information (optional)
    private String allergies;
    private String chronicConditions;

    // Emergency contact
    private String emergencyContactName;
    private String emergencyContactPhone;

    // Metadata - Changed to LocalDateTime to match entity
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    // Note: Aadhaar is NOT included for security reasons (PII)
}
