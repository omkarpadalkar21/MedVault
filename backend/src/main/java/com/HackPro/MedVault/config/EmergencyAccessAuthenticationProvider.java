package com.HackPro.MedVault.config;

import com.HackPro.MedVault.domain.entities.UserManagement.Doctor;
import com.HackPro.MedVault.domain.entities.UserManagement.VerificationStatus;
import com.HackPro.MedVault.repositories.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.List;

// Emergency access authentication provider
@RequiredArgsConstructor
public class EmergencyAccessAuthenticationProvider implements AuthenticationProvider {

    private final DoctorRepository doctorRepository;
    private final AadhaarVerificationService aadhaarService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        EmergencyAccessToken token = (EmergencyAccessToken) authentication;

        String licenseNumber = token.getLicenseNumber();
        String aadhaarNumber = token.getAadhaarNumber();
        String otp = token.getOtp();

        // Verify doctor exists and is verified
        Doctor doctor = doctorRepository.findByLicenseNumber((licenseNumber)
                .orElseThrow(() -> new BadCredentialsException("Invalid license number"));

        if (doctor.getVerificationStatus() != VerificationStatus.VERIFIED) {
            throw new BadCredentialsException("Doctor not verified");
        }

        // Verify Aadhaar OTP
        if (!aadhaarService.verifyOTP(aadhaarNumber, otp)) {
            throw new BadCredentialsException("Invalid Aadhaar OTP");
        }

        // Create authenticated token with limited emergency permissions
        List<GrantedAuthority> authorities = Arrays.asList(
                new SimpleGrantedAuthority("ROLE_DOCTOR"),
                new SimpleGrantedAuthority("PERM_EMERGENCY_ACCESS_ONLY")
        );

        return new EmergencyAccessToken(doctor, authorities);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return EmergencyAccessToken.class.isAssignableFrom(authentication);
    }
}

