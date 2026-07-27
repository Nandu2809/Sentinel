package com.sentinel.threat.repository;

import com.sentinel.threat.domain.entity.IncidentEntity;
import com.sentinel.threat.domain.model.IncidentStatus;
import com.sentinel.threat.domain.model.ThreatSeverity;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncidentRepository extends JpaRepository<IncidentEntity, UUID> {
    Page<IncidentEntity> findByStatus(IncidentStatus status, Pageable pageable);
    Page<IncidentEntity> findBySeverity(ThreatSeverity severity, Pageable pageable);
    long countBySeverityAndStatus(ThreatSeverity severity, IncidentStatus status);
    long countByCreatedAtAfter(Instant since);
}
