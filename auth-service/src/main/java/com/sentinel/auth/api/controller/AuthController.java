package com.sentinel.auth.api.controller;

import com.sentinel.auth.api.dto.AuthResponse;
import com.sentinel.auth.api.dto.ChangePasswordRequest;
import com.sentinel.auth.api.dto.EmailVerificationRequest;
import com.sentinel.auth.api.dto.ForgotPasswordRequest;
import com.sentinel.auth.api.dto.LoginRequest;
import com.sentinel.auth.api.dto.MessageResponse;
import com.sentinel.auth.api.dto.RefreshTokenRequest;
import com.sentinel.auth.api.dto.RegisterRequest;
import com.sentinel.auth.security.JwtPrincipal;
import com.sentinel.auth.service.AuthService;
import com.sentinel.auth.service.RequestMetadata;
import com.sentinel.common.api.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request,
                                                              HttpServletRequest servletRequest) {
        AuthResponse response = authService.register(request, RequestMetadata.from(servletRequest));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "Registration successful", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request,
                                                           HttpServletRequest servletRequest) {
        AuthResponse response = authService.login(request, RequestMetadata.from(servletRequest));
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Login successful", response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request,
                                                             HttpServletRequest servletRequest) {
        AuthResponse response = authService.refresh(request.refreshToken(), RequestMetadata.from(servletRequest));
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Token refreshed", response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<MessageResponse>> logout(@AuthenticationPrincipal JwtPrincipal principal,
                                                               HttpServletRequest servletRequest) {
        authService.logout(UUID.fromString(principal.userId()), RequestMetadata.from(servletRequest));
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Logout successful",
                new MessageResponse("Active refresh tokens revoked")));
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<MessageResponse>> changePassword(@AuthenticationPrincipal JwtPrincipal principal,
                                                                       @Valid @RequestBody ChangePasswordRequest request,
                                                                       HttpServletRequest servletRequest) {
        authService.changePassword(UUID.fromString(principal.userId()), request, RequestMetadata.from(servletRequest));
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Password changed",
                new MessageResponse("Password changed and sessions revoked")));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<MessageResponse>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request,
                                                                       HttpServletRequest servletRequest) {
        authService.requestPasswordReset(request, RequestMetadata.from(servletRequest));
        return ResponseEntity.accepted().body(ApiResponse.success(HttpStatus.ACCEPTED.value(),
                "Password reset request accepted",
                new MessageResponse("If the account exists, a reset workflow will be initiated")));
    }

    @PostMapping("/email-verification")
    public ResponseEntity<ApiResponse<MessageResponse>> emailVerification(
            @Valid @RequestBody EmailVerificationRequest request, HttpServletRequest servletRequest) {
        authService.requestEmailVerification(request, RequestMetadata.from(servletRequest));
        return ResponseEntity.accepted().body(ApiResponse.success(HttpStatus.ACCEPTED.value(),
                "Email verification request accepted",
                new MessageResponse("If the account exists, a verification workflow will be initiated")));
    }
}

