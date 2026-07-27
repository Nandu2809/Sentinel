package com.sentinel.threat.repository;

import com.sentinel.threat.domain.entity.DetectionEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetectionRepository extends JpaRepository<DetectionEntity, UUID> {
    List<DetectionEntity> findByThreatEventId(UUID threatEventId);
}
