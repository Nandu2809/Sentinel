package com.sentinel.report.repository;

import com.sentinel.report.domain.entity.ReportHistoryEntity;
import com.sentinel.report.domain.model.ReportType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportHistoryRepository extends JpaRepository<ReportHistoryEntity, UUID> {
    Optional<ReportHistoryEntity> findByReportCode(String reportCode);
    List<ReportHistoryEntity> findByReportTypeOrderByGeneratedAtDesc(ReportType reportType);
}
