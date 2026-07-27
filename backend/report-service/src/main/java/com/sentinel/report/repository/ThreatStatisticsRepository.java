package com.sentinel.report.repository;

import com.sentinel.report.domain.entity.ThreatStatisticsEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ThreatStatisticsRepository extends JpaRepository<ThreatStatisticsEntity, UUID> {
    List<ThreatStatisticsEntity> findTop10ByOrderByCountDesc();
}
