package com.HackPro.MedVault.services;

import com.HackPro.MedVault.domain.dtos.AuthResponseDto;
import com.HackPro.MedVault.domain.dtos.LoginRequestDto;
import com.HackPro.MedVault.security.MedVaultUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl {

    private final AuthenticationManager authenticationManager;
    private final MedVaultUserDetailsService userDetailsService;
    private final JwtService jwtService;

    public AuthResponseDto login(LoginRequestDto request, HttpServletRequest httpRequest) {
        String ipAddress = getClientIP(httpRequest);

        try {
            // Authenticate
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            // Record successful login
            userDetailsService.recordSuccessfulLogin(request.getEmail(), ipAddress);

            MedVaultUserDetails userDetails =
                    (MedVaultUserDetails) authentication.getPrincipal();

            // Generate tokens...
            return buildAuthResponse(userDetails);

        } catch (AuthenticationException e) {
            // Record failed login
            userDetailsService.recordFailedLogin(
                    request.getEmail(),
                    ipAddress,
                    e.getMessage()
            );
            throw new BadCredentialsException("Invalid email or password");
        }
    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }

    private AuthResponseDto buildAuthResponse(MedVaultUserDetails userDetails) {
        // Generate JWT tokens
        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);
        long expiresIn = jwtService.getAccessTokenExpiration() / 1000; // Convert to seconds

        // Build response with user information
        return AuthResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(expiresIn)
                .userId(userDetails.getUserId())
                .email(userDetails.getEmail())
                .role(userDetails.getRole())
                .authorities(userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .mfaEnabled(userDetails.isMfaEnabled())
                .mfaVerified(userDetails.getMfaVerified())
                .requiresMfa(userDetails.requiresMFA())
                .verificationStatus(userDetails.getVerificationStatus())
                .issuedAt(System.currentTimeMillis())
                .message("Login successful")
                .build();
    }
}

