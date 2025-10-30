package com.HackPro.MedVault.services;

import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.regex.Pattern;

// Password validation service
@Service
public class PasswordValidationService {

    private static final int MIN_LENGTH = 12;
    private static final Pattern UPPERCASE = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE = Pattern.compile("[a-z]");
    private static final Pattern DIGIT = Pattern.compile("[0-9]");
    private static final Pattern SPECIAL = Pattern.compile("[!@#$%^&*(),.?\":{}|<>]");

    public boolean isValidPassword(String password) {
        if (password == null || password.length() < MIN_LENGTH) {
            return false;
        }

        return UPPERCASE.matcher(password).find()
                && LOWERCASE.matcher(password).find()
                && DIGIT.matcher(password).find()
                && SPECIAL.matcher(password).find()
                && !isCommonPassword(password);
    }

    private boolean isCommonPassword(String password) {
        // Check against common password list
        Set<String> commonPasswords = Set.of("Password123!", "Admin@123");
        return commonPasswords.contains(password);
    }
}

