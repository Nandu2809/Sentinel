package com.sentinel.report.repository;

import com.sentinel.report.domain.entity.ScheduledReportEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduledReportRepository extends JpaRepository<ScheduledReportEntity, UUID> {
    List<ScheduledReportEntity> findByEnabledTrue();
}
