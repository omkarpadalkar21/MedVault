package com.HackPro.MedVault.controllers;

import com.HackPro.MedVault.domain.dtos.AuthResponseDto;
import com.HackPro.MedVault.domain.dtos.LoginRequestDto;
import com.HackPro.MedVault.domain.dtos.MFAVerificationRequestDto;
import com.HackPro.MedVault.domain.dtos.PatientRegistrationDto;
import com.HackPro.MedVault.services.AuthServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthServiceImpl authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(
            @Valid @RequestBody LoginRequestDto request,
            HttpServletRequest httpRequest) {
        return ResponseEntity.ok(authService.login(request, httpRequest));
    }

    @PostMapping("/verify-mfa")
    public ResponseEntity<AuthResponseDto> verifyMFA(
            @Valid @RequestBody MFAVerificationRequestDto request) {
        return ResponseEntity.ok(authService.verifyMFA(request));
    }

    @PostMapping("/register/patient")
    public ResponseEntity<AuthResponseDto> registerPatient(
            @Valid @RequestBody PatientRegistrationDto request) {
        return ResponseEntity.ok(authService.registerPatient(request));
    }
}
