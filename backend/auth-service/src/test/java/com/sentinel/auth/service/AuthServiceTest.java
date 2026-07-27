package com.sentinel.auth.service;

import com.sentinel.auth.api.dto.AuthResponse;
import com.sentinel.auth.api.dto.LoginRequest;
import com.sentinel.auth.api.dto.RegisterRequest;
import com.sentinel.auth.api.dto.RoleSummary;
import com.sentinel.auth.api.dto.UserResponse;
import com.sentinel.auth.domain.model.RoleEntity;
import com.sentinel.auth.domain.model.UserEntity;
import com.sentinel.auth.mapper.AuthMapper;
import com.sentinel.auth.repository.RoleRepository;
import com.sentinel.auth.repository.UserRepository;
import com.sentinel.auth.security.JwtTokenService;
import com.sentinel.auth.security.PasswordPolicyValidator;
import com.sentinel.auth.security.RefreshTokenService;
import com.sentinel.common.exception.BusinessException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private PasswordPolicyValidator passwordPolicyValidator;
    @Mock
    private JwtTokenService jwtTokenService;
    @Mock
    private RefreshTokenService refreshTokenService;
    @Mock
    private AuthMapper mapper;
    @Mock
    private AuditEventService auditEventService;

    private AuthService authService;
    private RequestMetadata metadata;
    private RoleEntity userRole;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository, roleRepository, passwordEncoder, passwordPolicyValidator,
                jwtTokenService, refreshTokenService, mapper, auditEventService);
        metadata = new RequestMetadata("127.0.0.1", "JUnit");
        userRole = new RoleEntity("USER", "Default user", true);
    }

    @Test
    void registerCreatesUserAndIssuesTokens() {
        RegisterRequest request = new RegisterRequest(
                "user@example.com",
                "user",
                "VeryStrong1!",
                "VeryStrong1!",
                "Test",
                "User");
        when(userRepository.existsByEmailIgnoreCase(request.email())).thenReturn(false);
        when(userRepository.existsByUsernameIgnoreCase(request.username())).thenReturn(false);
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode(request.password())).thenReturn("bcrypt-hash");
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtTokenService.createAccessToken(any(UserEntity.class))).thenReturn("access-token");
        when(jwtTokenService.accessTokenTtlSeconds()).thenReturn(900L);
        when(refreshTokenService.create(any(UserEntity.class), anyString())).thenReturn("refresh-token");
        when(mapper.toUserResponse(any(UserEntity.class))).thenReturn(userResponse());

        AuthResponse response = authService.register(request, metadata);

        assertEquals("access-token", response.accessToken());
        assertEquals("refresh-token", response.refreshToken());
        assertEquals(900L, response.expiresInSeconds());
        verify(passwordPolicyValidator).validateConfirmation(request.password(), request.confirmPassword());
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void registerRejectsDuplicateEmail() {
        RegisterRequest request = new RegisterRequest(
                "user@example.com",
                "user",
                "VeryStrong1!",
                "VeryStrong1!",
                "Test",
                "User");
        when(userRepository.existsByEmailIgnoreCase(request.email())).thenReturn(true);

        assertThrows(BusinessException.class, () -> authService.register(request, metadata));

        verify(userRepository, never()).save(any());
    }

    @Test
    void loginRejectsInvalidPasswordWithoutIssuingTokens() {
        UserEntity user = new UserEntity("user@example.com", "user", "bcrypt-hash", "Test", "User", userRole);
        user.activate();
        when(userRepository.findByEmailIgnoreCase("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-password", "bcrypt-hash")).thenReturn(false);

        assertThrows(BusinessException.class,
                () -> authService.login(new LoginRequest("user@example.com", "wrong-password"), metadata));

        verify(jwtTokenService, never()).createAccessToken(any());
        verify(refreshTokenService, never()).create(any(), anyString());
    }

    private UserResponse userResponse() {
        return new UserResponse(null, "user@example.com", "user", "Test", "User", null,
                true, new RoleSummary(null, "USER"), null, null);
    }
}

