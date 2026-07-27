package com.sentinel.risk.repository;

import com.sentinel.risk.domain.entity.EndpointRiskEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EndpointRiskRepository extends JpaRepository<EndpointRiskEntity, UUID> {
    Optional<EndpointRiskEntity> findByEndpoint(String endpoint);
    List<EndpointRiskEntity> findTop10ByOrderByRiskScoreDesc(Pageable pageable);
    List<EndpointRiskEntity> findByIsCriticalAssetTrue();
}
