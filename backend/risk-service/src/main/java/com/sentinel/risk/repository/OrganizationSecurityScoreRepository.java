package com.sentinel.risk.repository;

import com.sentinel.risk.domain.entity.OrganizationSecurityScoreEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationSecurityScoreRepository extends JpaRepository<OrganizationSecurityScoreEntity, UUID> {
    Optional<OrganizationSecurityScoreEntity> findTopByOrderByCalculatedAtDesc();
}
