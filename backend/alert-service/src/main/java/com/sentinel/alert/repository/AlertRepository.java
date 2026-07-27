package com.sentinel.alert.repository;

import com.sentinel.alert.domain.entity.AlertEntity;
import com.sentinel.alert.domain.model.AlertSeverity;
import com.sentinel.alert.domain.model.AlertStatus;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AlertRepository extends JpaRepository<AlertEntity, UUID> {
    Optional<AlertEntity> findByAlertCode(String alertCode);
    Optional<AlertEntity> findByCorrelationId(String correlationId);
    Page<AlertEntity> findByStatus(AlertStatus status, Pageable pageable);
    Page<AlertEntity> findBySeverity(AlertSeverity severity, Pageable pageable);

    @Query("SELECT a FROM AlertEntity a WHERE a.status = 'NEW' AND a.severity = :severity AND a.createdAt <= :threshold")
    List<AlertEntity> findUnacknowledgedAlertsPastSla(@Param("severity") AlertSeverity severity, @Param("threshold") Instant threshold);

    @Query("SELECT a.severity AS severity, COUNT(a) AS cnt FROM AlertEntity a GROUP BY a.severity")
    List<Object[]> countAlertsBySeverity();

    @Query("SELECT a.status AS status, COUNT(a) AS cnt FROM AlertEntity a GROUP BY a.status")
    List<Object[]> countAlertsByStatus();

    long countBySeverityAndStatus(AlertSeverity severity, AlertStatus status);
}
