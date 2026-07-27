package com.sentinel.auth.security;

import com.sentinel.common.exception.BusinessException;
import com.sentinel.common.exception.ErrorCode;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class PasswordPolicyValidator {
    private static final Pattern UPPER = Pattern.compile("[A-Z]");
    private static final Pattern LOWER = Pattern.compile("[a-z]");
    private static final Pattern DIGIT = Pattern.compile("\\d");
    private static final Pattern SPECIAL = Pattern.compile("[^a-zA-Z0-9]");

    public void validate(String password) {
        boolean valid = password != null
                && password.length() >= 12
                && UPPER.matcher(password).find()
                && LOWER.matcher(password).find()
                && DIGIT.matcher(password).find()
                && SPECIAL.matcher(password).find();
        if (!valid) {
            throw new BusinessException(
                    ErrorCode.SECURITY_POLICY_VIOLATION,
                    "Password must be at least 12 characters and include upper, lower, digit, and special characters");
        }
    }

    public void validateConfirmation(String password, String confirmation) {
        if (password == null || !password.equals(confirmation)) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, "Password confirmation does not match");
        }
        validate(password);
    }
}

