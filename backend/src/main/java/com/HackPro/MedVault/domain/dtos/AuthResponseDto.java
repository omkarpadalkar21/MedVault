package com.HackPro.MedVault.domain.dtos;

import com.HackPro.MedVault.domain.entities.UserManagement.UserRole;
import com.HackPro.MedVault.domain.entities.UserManagement.VerificationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDto {
    private String accessToken;
    private String refreshToken;

    @Builder.Default
    private String tokenType = "Bearer";

    private Long expiresIn;
    private UUID userId;
    private String email;
    private UserRole role;
    private List<String> authorities;

    @Builder.Default
    private boolean mfaEnabled = false;

    @Builder.Default
    private boolean mfaVerified = false;

    @Builder.Default
    private boolean requiresMfa = false;

    private VerificationStatus verificationStatus;
    private Long issuedAt;
    private String message;

    @Builder.Default
    private boolean isNewDevice = false;
}
