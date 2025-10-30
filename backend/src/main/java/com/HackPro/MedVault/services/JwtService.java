package com.HackPro.MedVault.services;

import com.HackPro.MedVault.domain.entities.UserManagement.UserRole;
import com.HackPro.MedVault.security.MedVaultUserDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration; // e.g., 900000 (15 minutes)

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration; // e.g., 604800000 (7 days)

    @Value("${jwt.temp-token-expiration:300000}") // 5 minutes for MFA
    private long tempTokenExpiration;

    // Track last activity for session timeout
    private final Map<String, Long> lastActivityMap = new ConcurrentHashMap<>();

    /**
     * Generate access token with full claims
     */
    public String generateAccessToken(MedVaultUserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userDetails.getUserId().toString());
        claims.put("role", userDetails.getRole().toString());
        claims.put("mfaVerified", userDetails.isMfaVerified());
        claims.put("tokenType", "access");
        claims.put("authorities", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        return buildToken(claims, userDetails.getEmail(), accessTokenExpiration);
    }

    /**
     * Generate refresh token with minimal claims
     */
    public String generateRefreshToken(MedVaultUserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userDetails.getUserId().toString());
        claims.put("tokenType", "refresh");

        return buildToken(claims, userDetails.getEmail(), refreshTokenExpiration);
    }

    /**
     * Generate temporary token for MFA flow (short-lived)
     */
    public String generateTempToken(MedVaultUserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userDetails.getUserId().toString());
        claims.put("tokenType", "temp");
        claims.put("mfaPending", true);

        return buildToken(claims, userDetails.getEmail(), tempTokenExpiration);
    }

    /**
     * Build JWT token with claims
     */
    private String buildToken(Map<String, Object> claims, String subject, long expiration) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extract username (email) from token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extract user ID from token
     */
    public UUID extractUserId(String token) {
        String userIdStr = extractClaim(token, claims -> claims.get("userId", String.class));
        return UUID.fromString(userIdStr);
    }

    /**
     * Extract user role from token
     */
    public UserRole extractRole(String token) {
        String roleStr = extractClaim(token, claims -> claims.get("role", String.class));
        return roleStr != null ? UserRole.valueOf(roleStr) : null;
    }

    /**
     * Extract token type
     */
    public String extractTokenType(String token) {
        return extractClaim(token, claims -> claims.get("tokenType", String.class));
    }

    /**
     * Extract specific claim from token
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extract all claims from token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Validate access token
     */
    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractAllClaims(token);

            // Check if token is expired
            if (claims.getExpiration().before(new Date())) {
                return false;
            }

            // Check token type
            String tokenType = claims.get("tokenType", String.class);
            if (!"access".equals(tokenType)) {
                return false;
            }

            // Check session timeout (15 minutes inactivity)
            Long lastActivity = lastActivityMap.get(token);
            if (lastActivity != null) {
                long inactiveTime = System.currentTimeMillis() - lastActivity;
                if (inactiveTime > 15 * 60 * 1000) { // 15 minutes
                    log.warn("Token expired due to inactivity");
                    return false;
                }
            }

            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Validate refresh token
     */
    public boolean isValidRefreshToken(String token) {
        try {
            Claims claims = extractAllClaims(token);

            // Check if token is expired
            if (claims.getExpiration().before(new Date())) {
                return false;
            }

            // Check token type
            String tokenType = claims.get("tokenType", String.class);
            return "refresh".equals(tokenType);

        } catch (JwtException | IllegalArgumentException e) {
            log.error("Refresh token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Validate temporary token (for MFA)
     */
    public boolean isValidTempToken(String token) {
        try {
            Claims claims = extractAllClaims(token);

            // Check if token is expired
            if (claims.getExpiration().before(new Date())) {
                return false;
            }

            // Check token type
            String tokenType = claims.get("tokenType", String.class);
            Boolean mfaPending = claims.get("mfaPending", Boolean.class);

            return "temp".equals(tokenType) && Boolean.TRUE.equals(mfaPending);

        } catch (JwtException | IllegalArgumentException e) {
            log.error("Temp token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Check if token has MFA verified
     */
    public boolean isMFAVerifiedToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            Boolean mfaVerified = claims.get("mfaVerified", Boolean.class);
            return Boolean.TRUE.equals(mfaVerified);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Update last activity timestamp for session management
     */
    public void updateLastActivity(String token) {
        lastActivityMap.put(token, System.currentTimeMillis());
    }

    /**
     * Invalidate token (logout)
     */
    public void invalidateToken(String token) {
        lastActivityMap.remove(token);
    }

    /**
     * Get signing key for JWT
     */
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
