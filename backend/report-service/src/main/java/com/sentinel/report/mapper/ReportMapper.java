package com.sentinel.report.mapper;

import com.sentinel.report.domain.entity.DashboardSnapshotEntity;
import com.sentinel.report.domain.entity.ReportHistoryEntity;
import com.sentinel.report.dto.DashboardOverviewResponse;
import com.sentinel.report.dto.ReportResponse;
import org.springframework.stereotype.Component;

@Component
public class ReportMapper {

    public DashboardOverviewResponse toDashboardOverviewResponse(DashboardSnapshotEntity entity) {
        if (entity == null) {
            return null;
        }
        return new DashboardOverviewResponse(
                entity.getTotalApisMonitored(),
                entity.getTotalRequests(),
                entity.getTotalThreatsDetected(),
                entity.getActiveIncidents(),
                entity.getCriticalAlerts(),
                entity.getCurrentSecurityScore(),
                entity.getRiskLevel(),
                entity.getCapturedAt()
        );
    }

    public ReportResponse toReportResponse(ReportHistoryEntity entity) {
        if (entity == null) {
            return null;
        }
        return new ReportResponse(
                entity.getId(),
                entity.getReportCode(),
                entity.getReportType(),
                entity.getFormat(),
                entity.getTitle(),
                entity.getSummary(),
                entity.getContentJson(),
                entity.getCreatedBy(),
                entity.getGeneratedAt()
        );
    }
}
