package com.sentinel.alert.incident.repository;

import com.sentinel.alert.domain.model.AlertSeverity;
import com.sentinel.alert.incident.domain.entity.IncidentEntity;
import com.sentinel.alert.incident.domain.model.IncidentStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IncidentRepository extends JpaRepository<IncidentEntity, UUID> {
    Optional<IncidentEntity> findByIncidentNumber(String incidentNumber);

    boolean existsByAlertId(UUID alertId);

    Optional<IncidentEntity> findByAlertId(UUID alertId);

    Page<IncidentEntity> findByStatus(IncidentStatus status, Pageable pageable);

    Page<IncidentEntity> findBySeverity(AlertSeverity severity, Pageable pageable);

    Page<IncidentEntity> findByAssignedAnalyst(String assignedAnalyst, Pageable pageable);

    long countByStatus(IncidentStatus status);

    long countBySeverity(AlertSeverity severity);

    @Query("SELECT i FROM IncidentEntity i WHERE " +
            "(:username IS NULL OR LOWER(i.affectedUser) LIKE LOWER(CONCAT('%', :username, '%'))) AND " +
            "(:ipAddress IS NULL OR LOWER(i.affectedIp) LIKE LOWER(CONCAT('%', :ipAddress, '%'))) AND " +
            "(:device IS NULL OR LOWER(i.affectedDevice) LIKE LOWER(CONCAT('%', :device, '%'))) AND " +
            "(:eventType IS NULL OR LOWER(i.title) LIKE LOWER(CONCAT('%', :eventType, '%')) OR LOWER(i.description) LIKE LOWER(CONCAT('%', :eventType, '%'))) AND " +
            "(:minRiskScore IS NULL OR i.riskScore >= :minRiskScore) AND " +
            "(:maxRiskScore IS NULL OR i.riskScore <= :maxRiskScore)")
    List<IncidentEntity> searchIncidents(
            @Param("username") String username,
            @Param("ipAddress") String ipAddress,
            @Param("device") String device,
            @Param("eventType") String eventType,
            @Param("minRiskScore") Double minRiskScore,
            @Param("maxRiskScore") Double maxRiskScore
    );
}
