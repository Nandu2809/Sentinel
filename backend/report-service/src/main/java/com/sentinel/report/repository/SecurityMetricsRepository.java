package com.sentinel.report.repository;

import com.sentinel.report.domain.entity.SecurityMetricsEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SecurityMetricsRepository extends JpaRepository<SecurityMetricsEntity, UUID> {
    List<SecurityMetricsEntity> findByCategoryOrderByTimestampDesc(String category);
}
