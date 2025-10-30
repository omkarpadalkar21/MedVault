package com.HackPro.MedVault.security;

import com.HackPro.MedVault.domain.entities.UserManagement.Doctor;
import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
public class EmergencyAccessToken extends AbstractAuthenticationToken {

    private final String licenseNumber;
    private final String aadhaarNumber;
    private final String otp;
    private final Doctor doctor;

    // Constructor for unauthenticated token
    public EmergencyAccessToken(String licenseNumber, String aadhaarNumber, String otp) {
        super(null);
        this.licenseNumber = licenseNumber;
        this.aadhaarNumber = aadhaarNumber;
        this.otp = otp;
        this.doctor = null;
        setAuthenticated(false);
    }

    // Constructor for authenticated token
    public EmergencyAccessToken(Doctor doctor, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.doctor = doctor;
        this.licenseNumber = doctor.getLicenseNumber();
        this.aadhaarNumber = null;
        this.otp = null;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return otp;
    }

    @Override
    public Object getPrincipal() {
        return doctor != null ? doctor : licenseNumber;
    }
}
