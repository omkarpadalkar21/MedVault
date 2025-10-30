package com.HackPro.MedVault.domain.entities.MedicalRecords;

import com.HackPro.MedVault.domain.entities.UserManagement.Patient;
import com.HackPro.MedVault.domain.entities.UserManagement.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "medical_records")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class MedicalRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Enumerated(EnumType.STRING)
    private RecordType recordType; // LAB_REPORT, PRESCRIPTION, RADIOLOGY, CLINICAL_NOTES

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    @Temporal(TemporalType.DATE)
    private Date recordDate;
    private String documentUrl; // S3/Cloud storage path

    @Column(columnDefinition = "TEXT")
    private String fhirResource; // Serialized FHIR resource (JSON)

    @Enumerated(EnumType.STRING)
    private EncryptionStatus encryptionStatus;

    private Boolean isEmergencyVisible = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by")
    private User uploadedBy;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    LocalDateTime updatedAt;
}