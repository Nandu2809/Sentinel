package com.sentinel.report.service;

import com.sentinel.report.domain.entity.ThreatStatisticsEntity;
import com.sentinel.report.dto.AnalyticsTrendResponse;
import com.sentinel.report.repository.ThreatStatisticsRepository;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SecurityAnalyticsService {
    private final ThreatStatisticsRepository threatStatisticsRepository;

    public SecurityAnalyticsService(ThreatStatisticsRepository threatStatisticsRepository) {
        this.threatStatisticsRepository = threatStatisticsRepository;
    }

    @Transactional(readOnly = true)
    public AnalyticsTrendResponse getSecurityScoreTrends() {
        List<Map<String, Object>> points = List.of(
                Map.of("timestamp", "2026-07-27T00:00:00Z", "score", 98.0),
                Map.of("timestamp", "2026-07-27T06:00:00Z", "score", 95.5),
                Map.of("timestamp", "2026-07-27T12:00:00Z", "score", 94.0)
        );
        return new AnalyticsTrendResponse("SECURITY_SCORE", "24h", points);
    }

    @Transactional(readOnly = true)
    public List<ThreatStatisticsEntity> getTopThreats() {
        return threatStatisticsRepository.findTop10ByOrderByCountDesc();
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getTopRiskAssets() {
        return List.of(
                Map.of("asset", "/api/v1/auth/login", "riskScore", 85.0, "type", "ENDPOINT"),
                Map.of("asset", "192.168.1.100", "riskScore", 78.0, "type", "CLIENT_IP")
        );
    }
}
