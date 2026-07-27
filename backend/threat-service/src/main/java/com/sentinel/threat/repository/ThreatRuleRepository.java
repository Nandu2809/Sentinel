package com.sentinel.threat.repository;

import com.sentinel.threat.domain.entity.ThreatRuleEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ThreatRuleRepository extends JpaRepository<ThreatRuleEntity, UUID> {
    Optional<ThreatRuleEntity> findByRuleCode(String ruleCode);
    List<ThreatRuleEntity> findByEnabledTrue();
    boolean existsByRuleCode(String ruleCode);
}
