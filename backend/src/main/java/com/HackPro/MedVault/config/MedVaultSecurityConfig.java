package com.HackPro.MedVault.config;

import com.HackPro.MedVault.security.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class MedVaultSecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Autowired
    private MFAAuthenticationFilter mfaAuthFilter;

    @Autowired
    private AuditLoggingFilter auditFilter;

    @Autowired
    private RateLimitingFilter rateLimitFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable for REST API, use CORS instead
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Stateless session for JWT
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                                .maximumSessions(1) // Enforce single session per user
                                .maxSessionsPreventsLogin(true)
                )

                // Healthcare-specific authorization rules
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers(
                                "/api/v1/auth/register/patient",
                                "/api/v1/auth/register/doctor",
                                "/api/v1/auth/login",
                                "/api/v1/auth/forgot-password",
                                "/api/v1/auth/reset-password",
                                "/api/v1/auth/refresh-token"
                        ).permitAll()

                        // Patient-only endpoints
                        .requestMatchers("/api/v1/patients/**")
                        .hasRole("PATIENT")

                        // Doctor-only endpoints
                        .requestMatchers("/api/v1/doctors/**")
                        .hasRole("DOCTOR")

                        // Emergency access - special handling
                        .requestMatchers("/api/v1/doctors/emergency-access")
                        .hasRole("DOCTOR")

                        // Admin endpoints
                        .requestMatchers("/api/v1/admin/**")
                        .hasRole("ADMIN")

                        // FHIR API endpoints - require authentication
                        .requestMatchers("/fhir/**")
                        .authenticated()

                        // All other requests must be authenticated
                        .anyRequest().authenticated()
                )

                // Add custom filters in order
                .addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(mfaAuthFilter, JwtAuthFilter.class)
                .addFilterAfter(auditFilter, MFAAuthenticationFilter.class)

                // Exception handling
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new MedVaultAuthenticationEntryPoint())
                        .accessDeniedHandler(new MedVaultAccessDeniedHandler())
                )

                // Logout configuration with audit
                .logout(logout -> logout
                        .logoutUrl("/api/v1/auth/logout")
                        .logoutSuccessHandler(new MedVaultLogoutSuccessHandler())
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID", "JWT-TOKEN")
                );

        return http.build();
    }

    // CORS configuration for healthcare app
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:5173"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH"));
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-MFA-Token"));
        config.setExposedHeaders(Arrays.asList("X-Total-Count", "X-Auth-Token"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    // Password encoder bean for healthcare security
    @Bean
    public PasswordEncoder passwordEncoder() {
        // Use BCrypt with strength 12 for healthcare (higher than default 10)
        return new BCryptPasswordEncoder(12);
    }
}

