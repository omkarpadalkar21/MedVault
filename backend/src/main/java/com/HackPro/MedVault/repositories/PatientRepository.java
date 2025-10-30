package com.HackPro.MedVault.repositories;

import com.HackPro.MedVault.domain.entities.UserManagement.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID> {

    boolean existsByAadhaarNumber(String encryptedAadhaarNumber);

    Optional<Patient> findByAadhaarNumber(String encryptedAadhaarNumber);
}
