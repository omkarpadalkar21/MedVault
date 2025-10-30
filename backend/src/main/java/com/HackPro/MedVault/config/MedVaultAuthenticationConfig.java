package com.HackPro.MedVault.config;

import com.HackPro.MedVault.repositories.DoctorRepository;
import com.HackPro.MedVault.services.AadhaarVerificationService;
import com.HackPro.MedVault.services.MedVaultUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
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

