package com.HackPro.MedVault.repositories;

import com.HackPro.MedVault.domain.entities.UserManagement.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PatientRepository extends JpaRepository<Patient, UUID> {
}
