package com.sentinel.report.service;

import com.sentinel.report.domain.entity.DashboardSnapshotEntity;
import com.sentinel.report.domain.model.RiskLevel;
import com.sentinel.report.dto.DashboardOverviewResponse;
import com.sentinel.report.mapper.ReportMapper;
import com.sentinel.report.repository.DashboardSnapshotRepository;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DashboardOverviewService {
    private final DashboardSnapshotRepository dashboardSnapshotRepository;
    private final ReportMapper mapper;

    public DashboardOverviewService(DashboardSnapshotRepository dashboardSnapshotRepository, ReportMapper mapper) {
        this.dashboardSnapshotRepository = dashboardSnapshotRepository;
        this.mapper = mapper;
    }

    @Transactional
    public DashboardOverviewResponse getOverview() {
        DashboardSnapshotEntity snapshot = dashboardSnapshotRepository.findTopByOrderByCapturedAtDesc()
                .orElseGet(() -> dashboardSnapshotRepository.save(new DashboardSnapshotEntity(
                        12, 14520, 3, 1, 0, 95.5, RiskLevel.LOW
                )));
        return mapper.toDashboardOverviewResponse(snapshot);
    }
}
