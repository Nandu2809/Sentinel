package com.sentinel.risk.repository;

import com.sentinel.risk.domain.entity.RiskRuleEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RiskRuleRepository extends JpaRepository<RiskRuleEntity, UUID> {
    Optional<RiskRuleEntity> findByRuleName(String ruleName);
}
