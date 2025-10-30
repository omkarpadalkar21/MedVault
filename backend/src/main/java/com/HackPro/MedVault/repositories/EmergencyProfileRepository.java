package com.HackPro.MedVault.repositories;

import com.HackPro.MedVault.domain.entities.MedicalRecords.EmergencyProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EmergencyProfileRepository extends JpaRepository<EmergencyProfile, UUID> {
}
