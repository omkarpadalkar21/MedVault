package com.HackPro.MedVault.services;

import com.HackPro.MedVault.security.MedVaultUserDetails;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

// JWT Service implementation
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration; // 15 minutes

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration; // 7 days

    private final Map<String, Long> lastActivityMap = new ConcurrentHashMap<>();

    public String generateAccessToken(MedVaultUserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userDetails.getUserId().toString());
        claims.put("role", userDetails.getRole().toString());
        claims.put("mfaVerified", userDetails.getMfaVerified());
        claims.put("authorities", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);

            // Check for session timeout (15 minutes of inactivity)
            Long lastActivity = lastActivityMap.get(token);
            if (lastActivity != null) {
                long inactiveTime = System.currentTimeMillis() - lastActivity;
                if (inactiveTime > 15 * 60 * 1000) { // 15 minutes
                    return false;
                }
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void updateLastActivity(String token) {
        lastActivityMap.put(token, System.currentTimeMillis());
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
