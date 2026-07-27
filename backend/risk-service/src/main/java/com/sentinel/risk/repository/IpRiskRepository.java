package com.sentinel.risk.repository;

import com.sentinel.risk.domain.entity.IpRiskEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IpRiskRepository extends JpaRepository<IpRiskEntity, UUID> {
    Optional<IpRiskEntity> findByClientIp(String clientIp);
    List<IpRiskEntity> findTop10ByOrderByRiskScoreDesc(Pageable pageable);
}
