package com.HackPro.MedVault.domain.dtos;

import com.HackPro.MedVault.domain.entities.UserManagement.Gender;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class PatientRegistrationDto {

    // Account credentials (from User entity)
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 12, message = "Password must be at least 12 characters")
    private String password;

    @NotBlank(message = "Password confirmation is required")
    private String confirmPassword;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone number must be valid")
    private String phoneNumber;

    // Patient personal information
    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotNull(message = "Date of birth is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    @NotNull(message = "Gender is required")
    private Gender gender;

    @NotBlank(message = "Aadhaar number is required")
    @Pattern(regexp = "^\\d{12}$", message = "Aadhaar number must be exactly 12 digits")
    private String aadhaarNumber;

    @NotBlank(message = "Blood group is required")
    @Pattern(
            regexp = "^(O_POSITIVE|O_NEGATIVE|A_POSITIVE|A_NEGATIVE|B_POSITIVE|B_NEGATIVE|AB_POSITIVE|AB_NEGATIVE)$",
            message = "Blood group must be valid"
    )
    private String bloodGroup;

    @NotBlank(message = "Address is required")
    private String address;

    // Optional medical information
    @Size(max = 1000, message = "Allergies description cannot exceed 1000 characters")
    private String allergies;

    @Size(max = 1000, message = "Chronic conditions description cannot exceed 1000 characters")
    private String chronicConditions;

    // Emergency contact information
    @NotBlank(message = "Emergency contact name is required")
    private String emergencyContactName;

    @NotBlank(message = "Emergency contact phone is required")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Emergency contact phone must be valid")
    private String emergencyContactPhone;

    // Terms and privacy acceptance
    @AssertTrue(message = "You must accept the terms of service")
    private Boolean termsAccepted;

    @AssertTrue(message = "You must accept the privacy policy")
    private Boolean privacyPolicyAccepted;

    // Validate that password and confirmPassword match
    public boolean isPasswordConfirmed() {
        return password != null && password.equals(confirmPassword);
    }

}
