package com.HackPro.MedVault.domain.entities.AuditAndSecurity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "login_attempts")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class LoginAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String ipAddress;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoginStatus status;

    private String failureReason;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date attemptedAt;
}
