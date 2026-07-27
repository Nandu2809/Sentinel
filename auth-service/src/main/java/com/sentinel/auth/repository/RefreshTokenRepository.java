package com.sentinel.auth.repository;

import com.sentinel.auth.domain.model.RefreshTokenEntity;
import com.sentinel.auth.domain.model.UserEntity;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, UUID> {
    Optional<RefreshTokenEntity> findByTokenHash(String tokenHash);

    @Modifying
    @Query("update RefreshTokenEntity token set token.revokedAt = :revokedAt, token.revokedByIp = :ipAddress where token.user = :user and token.revokedAt is null")
    int revokeActiveTokensForUser(UserEntity user, Instant revokedAt, String ipAddress);
}

