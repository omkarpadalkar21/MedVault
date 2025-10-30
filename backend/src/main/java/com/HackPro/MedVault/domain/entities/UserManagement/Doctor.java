package com.HackPro.MedVault.domain.entities.UserManagement;

import com.HackPro.MedVault.domain.entities.AccessControl.AccessRequest;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "doctors")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@PrimaryKeyJoinColumn(name = "user_id")
public class Doctor extends Users {

    @Column(unique = true, nullable = false)
    private String licenseNumber;

    private String firstName;

    private String lastName;

    private String specialization;
    private String hospitalAffiliation;

    @Enumerated(EnumType.STRING)
    private VerificationStatus verificationStatus;

    @Temporal(TemporalType.DATE)
    private Date licenseExpiryDate;

    @OneToMany(mappedBy = "doctor")
    private List<AccessRequest> accessRequests;
}