package com.HackPro.MedVault.domain.entities.UserManagement;

import com.HackPro.MedVault.domain.entities.MedicalRecords.EmergencyProfile;
import com.HackPro.MedVault.domain.entities.MedicalRecords.MedicalRecord;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "patients")
@PrimaryKeyJoinColumn(name = "user_id")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class Patient extends Users {
    @Column(unique = true, nullable = false)
    private String aadhaarNumber; // Encrypted

    private String firstName;

    private String lastName;

    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String bloodGroup;

    private String address;

    @Column(length = 1000)
    private String allergies;

    @Column(length = 1000)
    private String chronicConditions;

    private String emergencyContactName;

    private String emergencyContactPhone;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    private List<MedicalRecord> medicalRecords;

    @OneToOne(mappedBy = "patient", cascade = CascadeType.ALL)
    private EmergencyProfile emergencyProfile;
}