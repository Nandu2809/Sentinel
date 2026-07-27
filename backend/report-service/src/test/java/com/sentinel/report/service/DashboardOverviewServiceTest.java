package com.sentinel.report.service;

import com.sentinel.report.domain.entity.DashboardSnapshotEntity;
import com.sentinel.report.domain.model.RiskLevel;
import com.sentinel.report.dto.DashboardOverviewResponse;
import com.sentinel.report.mapper.ReportMapper;
import com.sentinel.report.repository.DashboardSnapshotRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardOverviewServiceTest {

    @Mock private DashboardSnapshotRepository dashboardSnapshotRepository;
    private final ReportMapper mapper = new ReportMapper();
    private DashboardOverviewService service;

    @BeforeEach
    void setUp() {
        service = new DashboardOverviewService(dashboardSnapshotRepository, mapper);
    }

    @Test
    void getOverview_ReturnsSnapshot() {
        DashboardSnapshotEntity entity = new DashboardSnapshotEntity(
                15, 25000, 5, 2, 1, 92.0, RiskLevel.MEDIUM
        );
        when(dashboardSnapshotRepository.findTopByOrderByCapturedAtDesc()).thenReturn(Optional.of(entity));

        DashboardOverviewResponse response = service.getOverview();

        assertNotNull(response);
        assertEquals(15, response.totalApisMonitored());
        assertEquals(25000, response.totalRequests());
        assertEquals(92.0, response.currentSecurityScore());
    }
}
