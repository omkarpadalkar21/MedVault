package com.HackPro.MedVault.domain.dtos;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorRegistrationDto {

    // ==================== Account Credentials ====================


    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 12, message = "Password must be at least 12 characters")
    private String password;

    @NotBlank(message = "Password confirmation is required")
    private String confirmPassword;

    // ==================== Personal Information ====================

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone number must be valid")
    private String phoneNumber;

    @NotBlank(message = "Address is required")
    @Size(max = 500, message = "Address cannot exceed 500 characters")
    private String address;

    // ==================== Professional Information ====================

    @NotBlank(message = "Specialization is required")
    @Size(max = 100, message = "Specialization cannot exceed 100 characters")
    private String specialization;

    @NotBlank(message = "License number is required")
    @Size(max = 50, message = "License number cannot exceed 50 characters")
    private String licenseNumber;

    @NotNull(message = "License expiry date is required")
    @Future(message = "License expiry date must be in the future")
    private LocalDate licenseExpiryDate;

    @NotBlank(message = "Hospital affiliation is required")
    @Size(max = 200, message = "Hospital affiliation cannot exceed 200 characters")
    private String hospitalAffiliation;

    @NotNull(message = "Years of experience is required")
    @Min(value = 0, message = "Years of experience cannot be negative")
    @Max(value = 100, message = "Years of experience seems unrealistic")
    private Integer yearsOfExperience;

    // ==================== Document Information ====================
    // Note: Files are uploaded separately via multipart/form-data
    // Backend will receive these as MultipartFile parameters

    // licenseDocument - handled separately as MultipartFile
    // identityProof - handled separately as MultipartFile

    // ==================== Terms & Privacy ====================

    @NotNull(message = "Terms acceptance is required")
    @AssertTrue(message = "You must accept the terms of service")
    private Boolean termsAccepted;

    @NotNull(message = "Privacy policy acceptance is required")
    @AssertTrue(message = "You must accept the privacy policy")
    private Boolean privacyPolicyAccepted;

    // ==================== Helper Methods ====================

    /**
     * Validate that password and confirmPassword match
     */
    public boolean isPasswordConfirmed() {
        return password != null && password.equals(confirmPassword);
    }
}
