package com.sentinel.alert.incident.repository;

import com.sentinel.alert.incident.domain.entity.IncidentNoteEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IncidentNoteRepository extends JpaRepository<IncidentNoteEntity, UUID> {
    List<IncidentNoteEntity> findByIncidentIdOrderByCreatedAtDesc(UUID incidentId);
}
