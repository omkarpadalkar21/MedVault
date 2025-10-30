package com.HackPro.MedVault.services;

import com.HackPro.MedVault.domain.entities.UserManagement.Doctor;
import com.HackPro.MedVault.domain.entities.UserManagement.User;
import com.HackPro.MedVault.domain.entities.UserManagement.VerificationStatus;
import com.HackPro.MedVault.domain.entities.AuditAndSecurity.LoginAttempt;
import com.HackPro.MedVault.domain.entities.AuditAndSecurity.LoginStatus;
import com.HackPro.MedVault.repositories.UserRepository;
import com.HackPro.MedVault.repositories.LoginAttemptRepository;
import com.HackPro.MedVault.security.MedVaultUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class MedVaultUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final LoginAttemptRepository loginAttemptRepository;

    // Constants for security policies
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCKOUT_DURATION_MINUTES = 15;

    /**
     * Load user by email (username) for Spring Security authentication.
     * Performs comprehensive validation including account lock, active status,
     * and doctor-specific verification checks.
     *
     * @param email the email address (username) of the user
     * @return UserDetails implementation with user information and authorities
     * @throws UsernameNotFoundException if user not found or account has issues
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Loading user by email: {}", email);

        // Check if account is locked due to too many failed attempts
        if (isAccountLocked(email)) {
            log.warn("Account locked due to failed login attempts: {}", email);
            throw new AccountLockedException(
                    "Account is temporarily locked due to too many failed login attempts. " +
                            "Please try again in " + LOCKOUT_DURATION_MINUTES + " minutes."
            );
        }

        // Load user from database
        User user = userRepository.findByEmail((email)
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", email);
                    return new UsernameNotFoundException("User not found with email: " + email);
                });

        // Check if user account is active
        if (!user.getIsActive()) {
            log.warn("Attempt to login with disabled account: {}", email);
            throw new DisabledAccountException("Account is disabled. Please contact support.");
        }

        // For doctors, perform additional verification checks
        if (user instanceof Doctor) {
            validateDoctorAccount((Doctor) user);
        }

        // Get failed login attempts count
        int failedAttempts = getRecentFailedAttempts(email);

        // Create and return UserDetails (MFA not verified at this stage)
        log.debug("Successfully loaded user: {} with role: {}", email, user.getRole());
        return new MedVaultUserDetails(user, false, failedAttempts);
    }

    /**
     * Load user with MFA verification status.
     * Used after successful MFA verification to create fully authenticated user.
     *
     * @param email       the user's email
     * @param mfaVerified whether MFA has been verified
     * @return UserDetails with MFA verification status
     */
    @Transactional(readOnly = true)
    public UserDetails loadUserWithMFAStatus(String email, boolean mfaVerified) {
        log.debug("Loading user with MFA status: {} - MFA verified: {}", email, mfaVerified);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        int failedAttempts = getRecentFailedAttempts(email);
        return new MedVaultUserDetails(user, mfaVerified, failedAttempts);
    }

    /**
     * Check if account is locked due to too many failed login attempts.
     *
     * @param email the user's email
     * @return true if account is locked, false otherwise
     */
    private boolean isAccountLocked(String email) {
        LocalDateTime lockoutThreshold = LocalDateTime.now().minusMinutes(LOCKOUT_DURATION_MINUTES);
        Date thresholdDate = Date.from(lockoutThreshold.atZone(ZoneId.systemDefault()).toInstant());

        long failedAttempts = loginAttemptRepository
                .countByEmailAndStatusAndAttemptedAtAfter(
                        email,
                        LoginStatus.FAILED,
                        thresholdDate
                );

        boolean isLocked = failedAttempts >= MAX_FAILED_ATTEMPTS;

        if (isLocked) {
            log.warn("Account locked for email: {} with {} failed attempts", email, failedAttempts);
        }

        return isLocked;
    }

    /**
     * Get count of recent failed login attempts.
     *
     * @param email the user's email
     * @return number of failed attempts in the last lockout period
     */
    private int getRecentFailedAttempts(String email) {
        LocalDateTime lockoutThreshold = LocalDateTime.now().minusMinutes(LOCKOUT_DURATION_MINUTES);
        Date thresholdDate = Date.from(lockoutThreshold.atZone(ZoneId.systemDefault()).toInstant());

        return (int) loginAttemptRepository
                .countByEmailAndStatusAndAttemptedAtAfter(
                        email,
                        LoginStatus.FAILED,
                        thresholdDate
                );
    }

    /**
     * Validate doctor-specific account requirements.
     *
     * @param doctor the doctor entity to validate
     * @throws UsernameNotFoundException if validation fails
     */
    private void validateDoctorAccount(Doctor doctor) {
        // Check verification status
        if (doctor.getVerificationStatus() != VerificationStatus.VERIFIED) {
            log.warn("Doctor account not verified: {} - Status: {}",
                    doctor.getEmail(), doctor.getVerificationStatus());

            throw new DoctorNotVerifiedException(
                    "Doctor account is not verified. Current status: " +
                            doctor.getVerificationStatus() +
                            ". Please wait for admin verification or contact support."
            );
        }

        // Check license expiry
        if (doctor.getLicenseExpiryDate() != null &&
                doctor.getLicenseExpiryDate().before(new Date())) {

            log.warn("Doctor license expired: {} - Expired on: {}",
                    doctor.getEmail(), doctor.getLicenseExpiryDate());

            throw new LicenseExpiredException(
                    "Medical license has expired on " + doctor.getLicenseExpiryDate() +
                            ". Please renew your license and update your profile."
            );
        }

        log.debug("Doctor account validation successful: {}", doctor.getEmail());
    }

    /**
     * Record successful login attempt.
     * Called by AuthService after successful authentication.
     *
     * @param email     the user's email
     * @param ipAddress the IP address of the login attempt
     */
    @Transactional
    public void recordSuccessfulLogin(String email, String ipAddress) {
        log.info("Recording successful login for email: {} from IP: {}", email, ipAddress);

        LoginAttempt loginAttempt = LoginAttempt.builder()
                .email(email)
                .ipAddress(ipAddress)
                .status(LoginStatus.SUCCESS)
                .attemptedAt(new Date())
                .build();

        loginAttemptRepository.save(loginAttempt);
    }

    /**
     * Record failed login attempt.
     * Called by AuthService after authentication failure.
     *
     * @param email     the user's email
     * @param ipAddress the IP address of the login attempt
     * @param reason    the reason for failure
     */
    @Transactional
    public void recordFailedLogin(String email, String ipAddress, String reason) {
        log.warn("Recording failed login for email: {} from IP: {} - Reason: {}",
                email, ipAddress, reason);

        LoginAttempt loginAttempt = LoginAttempt.builder()
                .email(email)
                .ipAddress(ipAddress)
                .status(LoginStatus.FAILED)
                .failureReason(reason)
                .attemptedAt(new Date())
                .build();

        loginAttemptRepository.save(loginAttempt);

        // Check if this triggers account lockout
        int recentFailedAttempts = getRecentFailedAttempts(email);
        if (recentFailedAttempts >= MAX_FAILED_ATTEMPTS) {
            log.error("Account lockout triggered for email: {} after {} failed attempts",
                    email, recentFailedAttempts);
        }
    }

    /**
     * Clear login attempts for a user.
     * Can be called by admin to unlock an account.
     *
     * @param email the user's email
     */
    @Transactional
    public void clearLoginAttempts(String email) {
        log.info("Clearing login attempts for email: {}", email);

        LocalDateTime lockoutThreshold = LocalDateTime.now().minusMinutes(LOCKOUT_DURATION_MINUTES);
        Date thresholdDate = Date.from(lockoutThreshold.atZone(ZoneId.systemDefault()).toInstant());

        loginAttemptRepository.deleteByEmailAndAttemptedAtAfter(email, thresholdDate);
    }

    /**
     * Check if user exists by email.
     *
     * @param email the email to check
     * @return true if user exists, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean userExists(String email) {
        return userRepository.existsByEmail((email));
    }

    // Custom exceptions for better error handling

    public static class AccountLockedException extends UsernameNotFoundException {
        public AccountLockedException(String message) {
            super(message);
        }
    }

    public static class DisabledAccountException extends UsernameNotFoundException {
        public DisabledAccountException(String message) {
            super(message);
        }
    }

    public static class DoctorNotVerifiedException extends UsernameNotFoundException {
        public DoctorNotVerifiedException(String message) {
            super(message);
        }
    }

    public static class LicenseExpiredException extends UsernameNotFoundException {
        public LicenseExpiredException(String message) {
            super(message);
        }
    }
}

