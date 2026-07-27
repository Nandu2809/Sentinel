package com.sentinel.threat.repository;

import com.sentinel.threat.domain.entity.AttackPatternEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttackPatternRepository extends JpaRepository<AttackPatternEntity, UUID> {
    List<AttackPatternEntity> findByRuleCodeAndEnabledTrue(String ruleCode);
    List<AttackPatternEntity> findByEnabledTrue();
}
