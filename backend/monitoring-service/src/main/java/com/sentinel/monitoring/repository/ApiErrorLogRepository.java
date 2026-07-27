package com.sentinel.monitoring.repository;

import com.sentinel.monitoring.domain.model.ApiErrorLogEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApiErrorLogRepository extends JpaRepository<ApiErrorLogEntity, UUID> {
    List<ApiErrorLogEntity> findTop50ByOrderByTimestampDesc();
}
