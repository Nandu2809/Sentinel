package com.sentinel.alert.incident.repository;

import com.sentinel.alert.incident.domain.entity.IncidentActionEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IncidentActionRepository extends JpaRepository<IncidentActionEntity, UUID> {
    List<IncidentActionEntity> findByIncidentIdOrderByTimestampDesc(UUID incidentId);
}
