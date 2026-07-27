package com.sentinel.auth.security;

import com.sentinel.common.exception.BusinessException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PasswordPolicyValidatorTest {
    private final PasswordPolicyValidator validator = new PasswordPolicyValidator();

    @Test
    void acceptsStrongPassword() {
        assertDoesNotThrow(() -> validator.validate("VeryStrong1!"));
    }

    @Test
    void rejectsWeakPassword() {
        assertThrows(BusinessException.class, () -> validator.validate("weak-password"));
    }

    @Test
    void rejectsMismatchedConfirmation() {
        assertThrows(BusinessException.class,
                () -> validator.validateConfirmation("VeryStrong1!", "VeryStrong2!"));
    }
}

