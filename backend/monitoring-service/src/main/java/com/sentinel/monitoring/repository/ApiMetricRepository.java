package com.sentinel.monitoring.repository;

import com.sentinel.monitoring.domain.model.ApiMetricEntity;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApiMetricRepository extends JpaRepository<ApiMetricEntity, UUID> {
    List<ApiMetricEntity> findByApiIdAndTimeBucketBetweenOrderByTimeBucketAsc(UUID apiId, Instant start, Instant end);
}
