package com.HackPro.MedVault.domain.entities.AccessControl;

import com.HackPro.MedVault.domain.entities.UserManagement.Doctor;
import com.HackPro.MedVault.domain.entities.UserManagement.Patient;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "access_permissions")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class AccessPermission {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Enumerated(EnumType.STRING)
    private PermissionLevel permissionLevel; // FULL_ACCESS, LIMITED_ACCESS, NO_ACCESS

    @Temporal(TemporalType.TIMESTAMP)
    private Date validFrom;

    @Temporal(TemporalType.TIMESTAMP)
    private Date validUntil;

    private Boolean isRevoked = false;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}