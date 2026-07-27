package com.sentinel.risk.repository;

import com.sentinel.risk.domain.entity.UserRiskEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRiskRepository extends JpaRepository<UserRiskEntity, UUID> {
    Optional<UserRiskEntity> findByUserId(String userId);
    List<UserRiskEntity> findTop10ByOrderByRiskScoreDesc(Pageable pageable);
}
