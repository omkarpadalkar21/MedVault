package com.HackPro.MedVault.controller;

import com.HackPro.MedVault.domain.dtos.*;
import com.HackPro.MedVault.services.AuthServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthServiceImpl authService;

    @PostMapping("/register/patient")
    public ResponseEntity<AuthResponseDto> registerPatient(
            @Valid @RequestBody PatientRegistrationDto request) {
        return ResponseEntity.ok(authService.registerPatient(request));
    }

    @PostMapping("/register/doctor")
    public ResponseEntity<AuthResponseDto> registerDoctor(
            @Valid @RequestBody DoctorRegistrationDto request) {
        return ResponseEntity.ok(authService.registerDoctor(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(
            @Valid @RequestBody LoginRequestDto request,
            HttpServletRequest httpRequest) {
        return ResponseEntity.ok(authService.login(request, httpRequest));
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(
            @AuthenticationPrincipal UserDetails userDetails,
            HttpServletRequest request,
            HttpServletResponse response) {
        authService.logout(userDetails, request, response);
        return ResponseEntity.ok(Map.of(
                "message", "Logout successful",
                "timestamp", String.valueOf(System.currentTimeMillis())
        ));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponseDto> refreshToken(
            @RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        return ResponseEntity.ok(authService.refreshToken(refreshToken));
    }

    @PostMapping("/verify-mfa")
    public ResponseEntity<AuthResponseDto> verifyMFA(
            @Valid @RequestBody MFAVerificationRequestDto request) {
        return ResponseEntity.ok(authService.verifyMFA(request));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(
            @RequestBody Map<String, String> request) {
        String email = request.get("email");
        authService.initiatePasswordReset(email);
        return ResponseEntity.ok(Map.of(
                "message", "Password reset link sent to email",
                "email", email
        ));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(
            @RequestBody ResetPasswordDto request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(Map.of(
                "message", "Password reset successful"
        ));
    }
}
