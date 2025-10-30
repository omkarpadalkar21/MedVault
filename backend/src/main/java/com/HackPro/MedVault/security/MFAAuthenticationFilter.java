package com.HackPro.MedVault.security;

import com.HackPro.MedVault.services.AuditLogService;
import com.HackPro.MedVault.services.MFAService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// Additional: MFA Authentication Filter
@Component
@RequiredArgsConstructor
public class MFAAuthenticationFilter extends OncePerRequestFilter {

    private final MFAService mfaService;
    private final AuditLogService auditLogService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            MedVaultUserDetails userDetails = (MedVaultUserDetails) authentication.getPrincipal();

            // Check if this is a sensitive operation requiring re-authentication
            if (isSensitiveOperation(request) && !mfaService.hasRecentMFAVerification(userDetails.getUserId())) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("{\"error\": \"Re-authentication required for sensitive operation\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isSensitiveOperation(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();

        // Sensitive operations requiring re-authentication
        return (path.contains("/access-permissions") && method.equals("DELETE")) ||
                path.contains("/emergency-profile") ||
                path.contains("/export-data") ||
                (path.contains("/medical-records") && method.equals("DELETE"));
    }
}


