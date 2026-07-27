package com.sentinel.auth.service;

import com.sentinel.auth.api.dto.AuthResponse;
import com.sentinel.auth.api.dto.ChangePasswordRequest;
import com.sentinel.auth.api.dto.EmailVerificationRequest;
import com.sentinel.auth.api.dto.ForgotPasswordRequest;
import com.sentinel.auth.api.dto.LoginRequest;
import com.sentinel.auth.api.dto.RegisterRequest;
import com.sentinel.auth.domain.model.AuditOutcome;
import com.sentinel.auth.domain.model.RoleEntity;
import com.sentinel.auth.domain.model.UserEntity;
import com.sentinel.auth.domain.model.UserStatus;
import com.sentinel.auth.mapper.AuthMapper;
import com.sentinel.auth.repository.RoleRepository;
import com.sentinel.auth.repository.UserRepository;
import com.sentinel.auth.security.JwtTokenService;
import com.sentinel.auth.security.PasswordPolicyValidator;
import com.sentinel.auth.security.RefreshTokenService;
import com.sentinel.common.exception.BusinessException;
import com.sentinel.common.exception.ErrorCode;
import com.sentinel.common.security.SecurityEventType;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private static final int MAX_FAILED_LOGIN_ATTEMPTS = 5;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordPolicyValidator passwordPolicyValidator;
    private final JwtTokenService jwtTokenService;
    private final RefreshTokenService refreshTokenService;
    private final AuthMapper mapper;
    private final AuditEventService auditEventService;

    public AuthService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder,
                       PasswordPolicyValidator passwordPolicyValidator, JwtTokenService jwtTokenService,
                       RefreshTokenService refreshTokenService, AuthMapper mapper, AuditEventService auditEventService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.passwordPolicyValidator = passwordPolicyValidator;
        this.jwtTokenService = jwtTokenService;
        this.refreshTokenService = refreshTokenService;
        this.mapper = mapper;
        this.auditEventService = auditEventService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request, RequestMetadata metadata) {
        passwordPolicyValidator.validateConfirmation(request.password(), request.confirmPassword());
        ensureUniqueUser(request.email(), request.username());

        RoleEntity role = roleRepository.findByName("USER")
                .orElseThrow(() -> new BusinessException(ErrorCode.INTERNAL_ERROR, "Default USER role is not configured"));

        UserEntity user = new UserEntity(
                request.email(),
                request.username(),
                passwordEncoder.encode(request.password()),
                request.firstName(),
                request.lastName(),
                role);
        user.activate();
        UserEntity saved = userRepository.save(user);
        auditEventService.record(saved.getId(), SecurityEventType.USER_REGISTERED, AuditOutcome.SUCCESS, metadata,
                "User", saved.getId().toString(), "User registered");
        return issueTokens(saved, metadata);
    }

    @Transactional
    public AuthResponse login(LoginRequest request, RequestMetadata metadata) {
        UserEntity user = userRepository.findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> {
                    auditEventService.record(null, SecurityEventType.LOGIN_FAILED, AuditOutcome.FAILURE, metadata,
                            "User", request.email(), "Invalid credentials");
                    return new BusinessException(ErrorCode.INVALID_CREDENTIALS, "Invalid email or password");
                });

        ensureLoginAllowed(user);
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            user.markLoginFailed(MAX_FAILED_LOGIN_ATTEMPTS);
            auditEventService.record(user.getId(), SecurityEventType.LOGIN_FAILED, AuditOutcome.FAILURE, metadata,
                    "User", user.getId().toString(), "Invalid credentials");
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS, "Invalid email or password");
        }

        user.markLoginSucceeded();
        auditEventService.record(user.getId(), SecurityEventType.LOGIN_SUCCEEDED, AuditOutcome.SUCCESS, metadata,
                "User", user.getId().toString(), "Login succeeded");
        return issueTokens(user, metadata);
    }

    @Transactional
    public AuthResponse refresh(String rawRefreshToken, RequestMetadata metadata) {
        UserEntity user = refreshTokenService.resolveActiveUser(rawRefreshToken);
        ensureLoginAllowed(user);
        String newRefreshToken = refreshTokenService.rotate(rawRefreshToken, user, metadata.ipAddress());
        String accessToken = jwtTokenService.createAccessToken(user);
        auditEventService.record(user.getId(), SecurityEventType.TOKEN_REFRESHED, AuditOutcome.SUCCESS, metadata,
                "User", user.getId().toString(), "Access token refreshed");
        return new AuthResponse(accessToken, newRefreshToken, "Bearer", jwtTokenService.accessTokenTtlSeconds(),
                mapper.toUserResponse(user));
    }

    @Transactional
    public void logout(UUID userId, RequestMetadata metadata) {
        UserEntity user = getUser(userId);
        refreshTokenService.revokeAll(user, metadata.ipAddress());
        auditEventService.record(user.getId(), SecurityEventType.LOGOUT, AuditOutcome.SUCCESS, metadata,
                "User", user.getId().toString(), "User logged out");
    }

    @Transactional
    public void changePassword(UUID userId, ChangePasswordRequest request, RequestMetadata metadata) {
        passwordPolicyValidator.validateConfirmation(request.newPassword(), request.confirmNewPassword());
        UserEntity user = getUser(userId);
        if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            auditEventService.record(user.getId(), SecurityEventType.LOGIN_FAILED, AuditOutcome.FAILURE, metadata,
                    "User", user.getId().toString(), "Invalid current password");
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS, "Current password is invalid");
        }
        user.changePassword(passwordEncoder.encode(request.newPassword()));
        refreshTokenService.revokeAll(user, metadata.ipAddress());
        auditEventService.record(user.getId(), SecurityEventType.PASSWORD_CHANGED, AuditOutcome.SUCCESS, metadata,
                "User", user.getId().toString(), "Password changed");
    }

    public void requestPasswordReset(ForgotPasswordRequest request, RequestMetadata metadata) {
        auditEventService.record(null, SecurityEventType.PASSWORD_RESET_REQUESTED, AuditOutcome.SUCCESS, metadata,
                "User", request.email(), "Password reset requested; delivery provider integration pending");
    }

    public void requestEmailVerification(EmailVerificationRequest request, RequestMetadata metadata) {
        auditEventService.record(null, SecurityEventType.EMAIL_VERIFICATION_REQUESTED, AuditOutcome.SUCCESS, metadata,
                "User", request.email(), "Email verification requested; delivery provider integration pending");
    }

    private AuthResponse issueTokens(UserEntity user, RequestMetadata metadata) {
        String accessToken = jwtTokenService.createAccessToken(user);
        String refreshToken = refreshTokenService.create(user, metadata.ipAddress());
        return new AuthResponse(accessToken, refreshToken, "Bearer", jwtTokenService.accessTokenTtlSeconds(),
                mapper.toUserResponse(user));
    }

    private void ensureUniqueUser(String email, String username) {
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new BusinessException(ErrorCode.DUPLICATE_RESOURCE, "Email is already registered");
        }
        if (userRepository.existsByUsernameIgnoreCase(username)) {
            throw new BusinessException(ErrorCode.DUPLICATE_RESOURCE, "Username is already registered");
        }
    }

    private void ensureLoginAllowed(UserEntity user) {
        if (user.getStatus() == UserStatus.LOCKED) {
            throw new BusinessException(ErrorCode.ACCOUNT_LOCKED, "Account is locked");
        }
        if (user.getStatus() == UserStatus.DISABLED) {
            throw new BusinessException(ErrorCode.ACCOUNT_DISABLED, "Account is disabled");
        }
    }

    private UserEntity getUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "User not found"));
    }
}

