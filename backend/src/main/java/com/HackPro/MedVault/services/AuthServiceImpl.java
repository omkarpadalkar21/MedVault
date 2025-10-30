package com.HackPro.MedVault.services;

import com.HackPro.MedVault.domain.dtos.AuthResponseDto;
import com.HackPro.MedVault.domain.dtos.LoginRequestDto;
import com.HackPro.MedVault.domain.dtos.PatientRegistrationDto;
import com.HackPro.MedVault.domain.entities.MedicalRecords.EmergencyProfile;
import com.HackPro.MedVault.domain.entities.UserManagement.Patient;
import com.HackPro.MedVault.domain.entities.UserManagement.UserRole;
import com.HackPro.MedVault.repositories.EmergencyProfileRepository;
import com.HackPro.MedVault.repositories.PatientRepository;
import com.HackPro.MedVault.repositories.UserRepository;
import com.HackPro.MedVault.security.MedVaultUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final EmergencyProfileRepository emergencyProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordValidationService passwordValidationService;
    private final EncryptionService encryptionService;
    private final AuditLogService auditLogService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    /**
     * Register a new patient in the system
     *
     * @param dto Patient registration data
     * @return AuthResponseDto with registration status
     * @throws ValidationException if validation fails
     * @throws DuplicateResourceException if user already exists
     */
    @Transactional
    public AuthResponseDto registerPatient(PatientRegistrationDto dto) {
        log.info("Starting patient registration for email: {}", dto.getEmail());

        // Step 1: Validate password confirmation
        if (!dto.isPasswordConfirmed()) {
            log.error("Password confirmation failed for email: {}", dto.getEmail());
            throw new ValidationException("Password and confirm password do not match");
        }

        // Step 2: Validate password strength
        if (!passwordValidationService.isValidPassword(dto.getPassword())) {
            log.error("Password validation failed for email: {}", dto.getEmail());
            throw new WeakPasswordException(
                    "Password must be at least 12 characters with uppercase, lowercase, digit, and special character"
            );
        }

        // Step 3: Check if email already exists
        if (userRepository.existsByEmail(dto.getEmail())) {
            log.error("Email already exists: {}", dto.getEmail());
            throw new DuplicateResourceException("Email is already registered");
        }

        // Step 4: Encrypt Aadhaar number for comparison
        String encryptedAadhaar = encryptionService.encrypt(dto.getAadhaarNumber());
        log.debug("Aadhaar encrypted for comparison");

        // Step 5: Check if Aadhaar already exists (compare encrypted values)
        if (patientRepository.existsByAadhaarNumber(encryptedAadhaar)) {
            log.error("Aadhaar number already registered: {}", maskAadhaar(dto.getAadhaarNumber()));
            throw new DuplicateResourceException("Aadhaar number is already registered");
        }

        // Step 6: Create Patient entity
        Patient patient = new Patient();

        // User base fields
        patient.setEmail(dto.getEmail());
        patient.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        patient.setPhoneNumber(dto.getPhoneNumber());
        patient.setRole(UserRole.PATIENT);
        patient.setIsActive(true);
        patient.setMfaEnabled(false); // MFA disabled by default, can be enabled later

        // Patient-specific fields
        patient.setFirstName(dto.getFirstName());
        patient.setLastName(dto.getLastName());
        patient.setDateOfBirth(Date.valueOf(dto.getDateOfBirth())); // Convert LocalDate to java.sql.Date
        patient.setGender(dto.getGender());
        patient.setAadhaarNumber(encryptedAadhaar); // Store encrypted Aadhaar
        patient.setBloodGroup(dto.getBloodGroup());
        patient.setAllergies(dto.getAllergies());
        patient.setChronicConditions(dto.getChronicConditions());
        patient.setAddress(dto.getAddress());
        patient.setEmergencyContactName(dto.getEmergencyContactName());
        patient.setEmergencyContactPhone(dto.getEmergencyContactPhone());

        // Step 7: Save patient to database
        Patient savedPatient = patientRepository.save(patient);
        log.info("Patient saved successfully with ID: {}", savedPatient.getId());

        // Step 8: Create default emergency profile
        createDefaultEmergencyProfile(savedPatient);

        // Step 9: Log registration event for audit
        auditLogService.logAuthenticationEvent(
                savedPatient.getId(),
                "PATIENT_REGISTERED",
                "system",
                java.util.Map.of(
                        "email", savedPatient.getEmail(),
                        "registrationTime", LocalDateTime.now().toString()
                )
        );

        // Step 10: Send verification email (TODO: implement email service)
        // emailService.sendVerificationEmail(savedPatient.getEmail());

        // Step 11: Return success response
        log.info("Patient registration completed successfully for: {}", dto.getEmail());

        return AuthResponseDto.builder()
                .userId(savedPatient.getId())
                .email(savedPatient.getEmail())
                .role(UserRole.PATIENT)
                .message("Registration successful! Welcome to MedVault.")
                .build();
    }

    /**
     * Create default emergency profile for newly registered patient
     * Copies critical information from patient profile
     */
    private void createDefaultEmergencyProfile(Patient patient) {
        EmergencyProfile emergencyProfile = new EmergencyProfile();
        emergencyProfile.setPatient(patient);
        emergencyProfile.setBloodGroup(patient.getBloodGroup());
        emergencyProfile.setCriticalAllergies(patient.getAllergies());
        emergencyProfile.setChronicDiseases(patient.getChronicConditions());
        emergencyProfile.setEmergencyContactName(patient.getEmergencyContactName());
        emergencyProfile.setEmergencyContactPhone(patient.getEmergencyContactPhone());
        emergencyProfile.setAdditionalNotes("Default emergency profile created during registration");

        emergencyProfileRepository.save(emergencyProfile);
        log.info("Emergency profile created for patient ID: {}", patient.getId());
    }

    /**
     * Mask Aadhaar number for logging (show only last 4 digits)
     */
    private String maskAadhaar(String aadhaar) {
        if (aadhaar == null || aadhaar.length() < 4) {
            return "****";
        }
        return "********" + aadhaar.substring(aadhaar.length() - 4);
    }

    public AuthResponseDto login(LoginRequestDto request, HttpServletRequest httpRequest) {
        String ipAddress = getClientIP(httpRequest);

        try {
            // Authenticate
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            // Record successful login
            userDetailsService.recordSuccessfulLogin(request.getEmail(), ipAddress);

            MedVaultUserDetails userDetails =
                    (MedVaultUserDetails) authentication.getPrincipal();

            // Generate tokens...
            return buildAuthResponse(userDetails);

        } catch (AuthenticationException e) {
            // Record failed login
            userDetailsService.recordFailedLogin(
                    request.getEmail(),
                    ipAddress,
                    e.getMessage()
            );
            throw new BadCredentialsException("Invalid email or password");
        }
    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }

    private AuthResponseDto buildAuthResponse(MedVaultUserDetails userDetails) {
        // Generate JWT tokens
        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);
        long expiresIn = jwtService.getAccessTokenExpiration() / 1000; // Convert to seconds

        // Build response with user information
        return AuthResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(expiresIn)
                .userId(userDetails.getUserId())
                .email(userDetails.getEmail())
                .role(userDetails.getRole())
                .authorities(userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .mfaEnabled(userDetails.isMfaEnabled())
                .mfaVerified(userDetails.isMfaVerified())
                .requiresMfa(userDetails.requiresMFA())
                .verificationStatus(userDetails.getVerificationStatus())
                .issuedAt(System.currentTimeMillis())
                .message("Login successful")
                .build();
    }
}

