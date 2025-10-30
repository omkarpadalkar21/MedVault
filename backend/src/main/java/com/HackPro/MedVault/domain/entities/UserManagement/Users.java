package com.HackPro.MedVault.domain.entities.UserManagement;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    private UserRole role; // PATIENT, DOCTOR, ADMIN

    @Column(nullable = false)
    private String phoneNumber;

    private Boolean mfaEnabled = false;

    private Boolean isActive = true;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
