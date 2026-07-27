package com.sentinel.auth.security;

import com.sentinel.auth.config.JwtProperties;
import com.sentinel.auth.domain.model.RefreshTokenEntity;
import com.sentinel.auth.domain.model.UserEntity;
import com.sentinel.auth.repository.RefreshTokenRepository;
import com.sentinel.common.exception.BusinessException;
import com.sentinel.common.exception.ErrorCode;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;
    private final SecureRandom secureRandom = new SecureRandom();

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, JwtProperties jwtProperties) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtProperties = jwtProperties;
    }

    @Transactional
    public String create(UserEntity user, String ipAddress) {
        byte[] randomBytes = new byte[64];
        secureRandom.nextBytes(randomBytes);
        String rawToken = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        refreshTokenRepository.save(new RefreshTokenEntity(
                hash(rawToken),
                user,
                Instant.now().plus(jwtProperties.refreshTokenTtl()),
                ipAddress));
        return rawToken;
    }

    @Transactional
    public String rotate(String rawToken, UserEntity user, String ipAddress) {
        RefreshTokenEntity existing = refreshTokenRepository.findByTokenHash(hash(rawToken))
                .orElseThrow(() -> new BusinessException(ErrorCode.TOKEN_INVALID, "Refresh token is invalid"));
        if (!existing.isActive(Instant.now()) || !existing.getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.TOKEN_INVALID, "Refresh token is invalid or expired");
        }

        String replacementRawToken = create(user, ipAddress);
        existing.revoke(ipAddress, hash(replacementRawToken));
        return replacementRawToken;
    }

    @Transactional(readOnly = true)
    public UserEntity resolveActiveUser(String rawToken) {
        RefreshTokenEntity existing = refreshTokenRepository.findByTokenHash(hash(rawToken))
                .orElseThrow(() -> new BusinessException(ErrorCode.TOKEN_INVALID, "Refresh token is invalid"));
        if (!existing.isActive(Instant.now())) {
            throw new BusinessException(ErrorCode.TOKEN_INVALID, "Refresh token is invalid or expired");
        }
        return existing.getUser();
    }

    @Transactional
    public void revokeAll(UserEntity user, String ipAddress) {
        refreshTokenRepository.revokeActiveTokensForUser(user, Instant.now(), ipAddress);
    }

    public String hash(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hashed);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 algorithm unavailable", ex);
        }
    }
}
