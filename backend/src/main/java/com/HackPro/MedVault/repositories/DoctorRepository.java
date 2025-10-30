package com.HackPro.MedVault.repositories;

import com.HackPro.MedVault.domain.entities.UserManagement.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DoctorRepository extends JpaRepository<Doctor, UUID> {
}
