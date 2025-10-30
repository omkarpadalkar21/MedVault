package com.HackPro.MedVault.repositories;

import com.HackPro.MedVault.domain.entities.AuditAndSecurity.LoginAttempt;
import com.HackPro.MedVault.domain.entities.AuditAndSecurity.LoginStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.UUID;

@Repository
public interface LoginAttemptRepository extends JpaRepository<LoginAttempt, UUID> {

    /**
     * Count failed login attempts after a specific date
     */
    long countByEmailAndStatusAndAttemptedAtAfter(
            String email,
            LoginStatus status,
            Date attemptedAt
    );

    /**
     * Delete login attempts for cleanup (admin unlock)
     */
    void deleteByEmailAndAttemptedAtAfter(String email, Date attemptedAt);

    /**
     * Find all login attempts for a user (for audit purposes)
     */
    java.util.List<LoginAttempt> findByEmailOrderByAttemptedAtDesc(String email);
}

