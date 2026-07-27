package com.sentinel.threat.repository;

import com.sentinel.threat.domain.entity.RuleExecutionLogEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RuleExecutionLogRepository extends JpaRepository<RuleExecutionLogEntity, UUID> {
}
