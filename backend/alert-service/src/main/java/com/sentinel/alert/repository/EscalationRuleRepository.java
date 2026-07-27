package com.sentinel.alert.repository;

import com.sentinel.alert.domain.entity.EscalationRuleEntity;
import com.sentinel.alert.domain.model.AlertSeverity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EscalationRuleRepository extends JpaRepository<EscalationRuleEntity, UUID> {
    Optional<EscalationRuleEntity> findBySeverityAndEnabledTrue(AlertSeverity severity);
    List<EscalationRuleEntity> findByEnabledTrue();
}
