package com.HackPro.MedVault.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class MedVaultLogoutSuccessHandler implements LogoutSuccessHandler {
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                                org.springframework.security.core.Authentication authentication)
            throws IOException {
        if (authentication != null) {
            log.info("User logged out: {}", authentication.getName());
        }

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);

        Map<String, Object> data = new HashMap<>();
        data.put("message", "Logout successful");
        data.put("timestamp", System.currentTimeMillis());

        new ObjectMapper().writeValue(response.getOutputStream(), data);
    }
}


