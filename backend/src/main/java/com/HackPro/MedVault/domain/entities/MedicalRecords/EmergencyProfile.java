package com.HackPro.MedVault.domain.entities.MedicalRecords;

import com.HackPro.MedVault.domain.entities.UserManagement.Patient;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "emergency_profiles")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class EmergencyProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    private String bloodGroup;

    @Column(length = 1000)
    private String criticalAllergies;

    @Column(length = 1000)
    private String chronicDiseases;

    @Column(length = 1000)
    private String currentMedications;

    private String emergencyContactName;

    private String emergencyContactPhone;

    @Column(columnDefinition = "TEXT")
    private String additionalNotes;

    @UpdateTimestamp
    private LocalDateTime lastUpdated;
}