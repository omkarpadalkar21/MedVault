package com.HackPro.MedVault.security;

import com.HackPro.MedVault.services.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final MedVaultUserDetailsService userDetailsService;
    private final AuditLogService auditLogService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // Skip JWT validation for public endpoints
        if (isPublicEndpoint(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract JWT from header
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);

        try {
            userEmail = jwtService.extractUsername(jwt);

            // If user is not authenticated yet
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Validate JWT
                if (!jwtService.isTokenValid(jwt)) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("{\"error\": \"Invalid or expired token\"}");
                    return;
                }

                // Load user details
                UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
                MedVaultUserDetails medVaultUser = (MedVaultUserDetails) userDetails;

                // Check if MFA is required but not verified
                if (medVaultUser.requiresMFA() && !jwtService.isMFAVerifiedToken(jwt)) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("{\"error\": \"MFA verification required\"}");
                    return;
                }

                // Create authentication token
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                // Set authentication details
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Set authentication in security context
                SecurityContextHolder.getContext().setAuthentication(authToken);

                // Update last activity time for session management
                jwtService.updateLastActivity(jwt);
            }

        } catch (ExpiredJwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Token expired\"}");
            return;
        } catch (Exception e) {
            auditLogService.logSecurityEvent("JWT_VALIDATION_FAILED", request.getRemoteAddr(), e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Authentication failed\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPublicEndpoint(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/api/v1/auth/register") ||
                path.startsWith("/api/v1/auth/login") ||
                path.startsWith("/api/v1/auth/forgot-password") ||
                path.equals("/fhir/metadata");
    }
}

