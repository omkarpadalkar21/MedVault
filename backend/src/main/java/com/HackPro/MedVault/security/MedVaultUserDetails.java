package com.HackPro.MedVault.security;

import com.HackPro.MedVault.domain.entities.UserManagement.Doctor;
import com.HackPro.MedVault.domain.entities.UserManagement.User;
import com.HackPro.MedVault.domain.entities.UserManagement.UserRole;
import com.HackPro.MedVault.domain.entities.UserManagement.VerificationStatus;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Getter
public class MedVaultUserDetails implements UserDetails {

    private final UUID userId;
    private final String email;
    private final String passwordHash;  // Renamed for clarity
    private final UserRole role;
    private final boolean mfaEnabled;
    private final boolean mfaVerified;
    private final VerificationStatus verificationStatus; // For doctors
    private final Collection<? extends GrantedAuthority> authorities;
    private final boolean accountNonLocked;
    private final int failedLoginAttempts;


    public MedVaultUserDetails(User user, boolean mfaVerified, int failedLoginAttempts) {
        this.userId = user.getId();
        this.email = user.getEmail();
        this.passwordHash = user.getPasswordHash();
        this.role = user.getRole();
        this.mfaEnabled = user.getMfaEnabled();
        this.mfaVerified = mfaVerified;
        this.failedLoginAttempts = failedLoginAttempts;
        this.accountNonLocked = user.getIsActive() && failedLoginAttempts < 5;

        // For doctors, check verification status
        if (user instanceof Doctor) {
            this.verificationStatus = ((Doctor) user).getVerificationStatus();
        } else {
            this.verificationStatus = null;
        }

        // Set authorities based on role and verification
        this.authorities = getAuthoritiesForUser(user);
    }

    private Collection<? extends GrantedAuthority> getAuthoritiesForUser(User user) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        // Base role authority
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.name()));

        // Additional granular permissions based on role
        switch (role) {
            case PATIENT:
                authorities.add(new SimpleGrantedAuthority("PERM_VIEW_OWN_RECORDS"));
                authorities.add(new SimpleGrantedAuthority("PERM_MANAGE_PERMISSIONS"));
                authorities.add(new SimpleGrantedAuthority("PERM_GRANT_ACCESS"));
                break;

            case DOCTOR:
                // Only verified doctors get access permissions
                if (verificationStatus == VerificationStatus.VERIFIED) {
                    authorities.add(new SimpleGrantedAuthority("PERM_REQUEST_ACCESS"));
                    authorities.add(new SimpleGrantedAuthority("PERM_VIEW_PATIENT_RECORDS"));
                    authorities.add(new SimpleGrantedAuthority("PERM_ADD_CLINICAL_NOTES"));
                }
                // Emergency access for all doctors
                authorities.add(new SimpleGrantedAuthority("PERM_EMERGENCY_ACCESS"));
                break;

            case ADMIN:
                authorities.add(new SimpleGrantedAuthority("PERM_MANAGE_USERS"));
                authorities.add(new SimpleGrantedAuthority("PERM_VIEW_AUDIT_LOGS"));
                authorities.add(new SimpleGrantedAuthority("PERM_VERIFY_DOCTORS"));
                authorities.add(new SimpleGrantedAuthority("PERM_SYSTEM_CONFIG"));
                break;
        }

        return authorities;
    }

    // UserDetails interface methods
    @Override
    public String getUsername() {
        return email;  // ✅ Return email as username
    }

    @Override
    public String getPassword() {
        return passwordHash;  // ✅ Return the hashed password
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;  // Lombok @Getter also provides this, but explicit for clarity
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // Could check password age here for forced rotation
        return true;
    }

    @Override
    public boolean isEnabled() {
        // Account is enabled if:
        // 1. User is active (accountNonLocked)
        // 2. For doctors: must NOT be rejected (pending and verified are allowed)
        // 3. MFA must be verified if enabled
        if (role == UserRole.DOCTOR) {
            return accountNonLocked &&
                    verificationStatus != VerificationStatus.REJECTED &&
                    (!mfaEnabled || mfaVerified);
        }
        return accountNonLocked && (!mfaEnabled || mfaVerified);
    }

    // Additional helper methods
    public boolean requiresMFA() {
        return mfaEnabled && !mfaVerified;
    }

    public boolean isDoctorVerified() {
        return role != UserRole.DOCTOR || verificationStatus == VerificationStatus.VERIFIED;
    }
}
