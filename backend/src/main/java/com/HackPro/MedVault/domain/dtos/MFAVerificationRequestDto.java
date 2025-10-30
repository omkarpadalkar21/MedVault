package com.HackPro.MedVault.domain.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MFA verification request DTO (Step 2 of authentication)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MFAVerificationRequestDto {

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "MFA code is required")
    @Pattern(regexp = "^[0-9]{6}$", message = "MFA code must be 6 digits")
    private String mfaCode;

    @NotBlank(message = "Temporary token is required")
    private String tempToken; // Token received from step 1
}

