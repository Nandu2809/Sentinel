package com.sentinel.alert.incident.repository;

import com.sentinel.alert.incident.domain.entity.IncidentTimelineEventEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IncidentTimelineEventRepository extends JpaRepository<IncidentTimelineEventEntity, UUID> {
    List<IncidentTimelineEventEntity> findByIncidentIdOrderByTimestampAsc(UUID incidentId);
}
