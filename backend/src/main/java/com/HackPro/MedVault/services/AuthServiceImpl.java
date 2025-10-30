package com.HackPro.MedVault.services;

import com.HackPro.MedVault.domain.dtos.AuthResponseDto;
import com.HackPro.MedVault.domain.dtos.DoctorRegistrationDto;
import com.HackPro.MedVault.domain.dtos.LoginRequestDto;
import com.HackPro.MedVault.domain.dtos.MFAVerificationRequestDto;
import com.HackPro.MedVault.domain.dtos.PatientRegistrationDto;
import com.HackPro.MedVault.domain.dtos.ResetPasswordDto;
import com.HackPro.MedVault.domain.entities.MedicalRecords.EmergencyProfile;
import com.HackPro.MedVault.domain.entities.UserManagement.Doctor;
import com.HackPro.MedVault.domain.entities.UserManagement.Patient;
import com.HackPro.MedVault.domain.entities.UserManagement.UserRole;
import com.HackPro.MedVault.domain.entities.UserManagement.VerificationStatus;
import com.HackPro.MedVault.exceptions.DuplicateResourceException;
import com.HackPro.MedVault.exceptions.WeakPasswordException;
import com.HackPro.MedVault.repositories.DoctorRepository;
import com.HackPro.MedVault.repositories.EmergencyProfileRepository;
import com.HackPro.MedVault.repositories.PatientRepository;
import com.HackPro.MedVault.repositories.UserRepository;
import com.HackPro.MedVault.security.MedVaultUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final MedVaultUserDetailsService MedVaultUserDetailsService;
    private final JwtService jwtService;
    private final DoctorRepository doctorRepository;
    private final MFAService mfaService;

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
            MedVaultUserDetailsService.recordSuccessfulLogin(request.getEmail(), ipAddress);

            MedVaultUserDetails userDetails =
                    (MedVaultUserDetails) authentication.getPrincipal();

            // Generate tokens...
            return buildAuthResponse(userDetails);

        } catch (AuthenticationException e) {
            // Record failed login
            MedVaultUserDetailsService.recordFailedLogin(
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

    // Add these methods to your existing AuthServiceImpl class

    @Transactional
    public AuthResponseDto registerDoctor(DoctorRegistrationDto dto) {
        log.info("Starting doctor registration for email: {}", dto.getEmail());

        // Validate password confirmation
        if (!dto.isPasswordConfirmed()) {
            throw new ValidationException("Password and confirm password do not match");
        }

        // Validate password strength
        if (!passwordValidationService.isValidPassword(dto.getPassword())) {
            throw new WeakPasswordException(
                    "Password must be at least 12 characters with uppercase, lowercase, digit, and special character"
            );
        }

        // Check if email already exists
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("Email is already registered");
        }

        // Create Doctor entity
        Doctor doctor = new Doctor();
        doctor.setEmail(dto.getEmail());
        doctor.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        doctor.setPhoneNumber(dto.getPhoneNumber());
        doctor.setRole(UserRole.DOCTOR);
        doctor.setIsActive(true);
        doctor.setMfaEnabled(false);

        doctor.setFirstName(dto.getFirstName());
        doctor.setLastName(dto.getLastName());
        doctor.setSpecialization(dto.getSpecialization());
        doctor.setLicenseNumber(dto.getLicenseNumber());
        doctor.setLicenseExpiryDate(java.sql.Date.valueOf(dto.getLicenseExpiryDate()));
        doctor.setHospitalAffiliation(dto.getHospitalAffiliation());
        doctor.setVerificationStatus(VerificationStatus.PENDING);

        Doctor savedDoctor = doctorRepository.save(doctor);
        log.info("Doctor registered successfully with ID: {}", savedDoctor.getId());

        return AuthResponseDto.builder()
                .userId(savedDoctor.getId())
                .email(savedDoctor.getEmail())
                .role(savedDoctor.getRole())
                .verificationStatus(savedDoctor.getVerificationStatus())
                .message("Doctor registration successful. Awaiting verification.")
                .issuedAt(System.currentTimeMillis())
                .build();
    }

    public void logout(UserDetails userDetails, HttpServletRequest request,
                       HttpServletResponse response) {
        if (userDetails != null) {
            log.info("User logged out: {}", userDetails.getUsername());
            auditLogService.logAuthenticationEvent(
                    ((MedVaultUserDetails) userDetails).getUserId(),
                    "LOGOUT",
                    request.getRemoteAddr()
            );
        }
    }

    public AuthResponseDto refreshToken(String refreshToken) {
        try {
            String email = jwtService.extractUsername(refreshToken);
            MedVaultUserDetails userDetails =
                    (MedVaultUserDetails) MedVaultUserDetailsService.loadUserByUsername(email);

            if (jwtService.isValidRefreshToken(refreshToken)) {
                String newAccessToken = jwtService.generateAccessToken(userDetails);

                return AuthResponseDto.builder()
                        .accessToken(newAccessToken)
                        .refreshToken(refreshToken)
                        .userId(userDetails.getUserId())
                        .email(userDetails.getEmail())
                        .role(userDetails.getRole())
                        .expiresIn(jwtService.getExpirationTime())
                        .issuedAt(System.currentTimeMillis())
                        .build();
            }
            throw new BadCredentialsException("Invalid refresh token");
        } catch (Exception e) {
            log.error("Token refresh failed", e);
            throw new BadCredentialsException("Token refresh failed");
        }
    }

    public AuthResponseDto verifyMFA(MFAVerificationRequestDto request) {
        MedVaultUserDetails userDetails =
                (MedVaultUserDetails) MedVaultUserDetailsService.loadUserByUsername(request.getEmail());

        if (mfaService.verifyMFACode(userDetails.getUserId(), request.getMfaCode())) {
            String accessToken = jwtService.generateAccessToken(userDetails);
            String refreshToken = jwtService.generateRefreshToken(userDetails);

            return AuthResponseDto.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .userId(userDetails.getUserId())
                    .email(userDetails.getEmail())
                    .role(userDetails.getRole())
                    .mfaVerified(true)
                    .expiresIn(jwtService.getExpirationTime())
                    .issuedAt(System.currentTimeMillis())
                    .build();
        }
        throw new BadCredentialsException("Invalid MFA code");
    }

    public void initiatePasswordReset(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            String resetToken = jwtService.generatePasswordResetToken(email);
            // TODO: Send email with reset link
            log.info("Password reset initiated for: {}", email);
            auditLogService.logActivity(user.getId(), "PASSWORD_RESET_REQUESTED", "SYSTEM");
        });
    }

    @Transactional
    public void resetPassword(ResetPasswordDto dto) {
        if (!dto.isPasswordConfirmed()) {
            throw new ValidationException("Passwords do not match");
        }

        if (!passwordValidationService.isValidPassword(dto.getNewPassword())) {
            throw new WeakPasswordException("Password does not meet security requirements");
        }

        String email = jwtService.extractUsername(dto.getResetToken());
        userRepository.findByEmail(email).ifPresent(user -> {
            user.setPasswordHash(passwordEncoder.encode(dto.getNewPassword()));
            userRepository.save(user);
            log.info("Password reset successful for: {}", email);
            auditLogService.logActivity(user.getId(), "PASSWORD_RESET_COMPLETED", "SYSTEM");
        });
    }

}

