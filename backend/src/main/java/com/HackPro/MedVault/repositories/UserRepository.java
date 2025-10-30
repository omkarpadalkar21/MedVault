package com.HackPro.MedVault.repositories;

import com.HackPro.MedVault.domain.entities.UserManagement.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
}
