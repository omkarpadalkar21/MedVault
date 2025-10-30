package com.HackPro.MedVault.config;

import com.HackPro.MedVault.domain.entities.UserManagement.Doctor;
import com.HackPro.MedVault.domain.entities.UserManagement.VerificationStatus;
import com.HackPro.MedVault.repositories.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class MedVaultAuthenticationConfig {

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(
            MedVaultUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        provider.setHideUserNotFoundExceptions(false); // For better error messages

        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // Custom authentication provider for emergency access
    @Bean
    public EmergencyAccessAuthenticationProvider emergencyAccessProvider(
            DoctorRepository doctorRepository,
            AadhaarVerificationService aadhaarService) {
        return new EmergencyAccessAuthenticationProvider(doctorRepository, aadhaarService);
    }
}

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
        Doctor doctor = doctorRepository.findByLicenseNumber(licenseNumber)
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

