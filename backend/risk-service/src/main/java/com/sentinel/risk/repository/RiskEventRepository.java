package com.sentinel.risk.repository;

import com.sentinel.risk.domain.entity.RiskEventEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RiskEventRepository extends JpaRepository<RiskEventEntity, UUID> {
}
