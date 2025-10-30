package com.HackPro.MedVault.domain.entities.AccessControl;

import com.HackPro.MedVault.domain.entities.UserManagement.Doctor;
import com.HackPro.MedVault.domain.entities.UserManagement.Patient;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "access_requests")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class AccessRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Enumerated(EnumType.STRING)
    private AccessRequestStatus status; // PENDING, APPROVED, DENIED, EXPIRED@Enumerated(EnumType.STRING)

    private AccessType accessType; // OTP_CONSENT, EMERGENCY_ACCESS

    private String otpCode; // For OTP-based access

    @Temporal(TemporalType.TIMESTAMP)
    private Date otpExpiryTime;
    @Column(length = 500)

    private String reasonForAccess;

    @Temporal(TemporalType.TIMESTAMP)
    private Date accessGrantedAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date accessExpiryTime;

    @CreationTimestamp
    private LocalDateTime createdAt;
}