package com.sentinel.threat.service;

import com.sentinel.threat.domain.model.IncidentStatus;
import com.sentinel.threat.domain.model.ThreatSeverity;
import com.sentinel.threat.dto.ThreatStatisticsResponse;
import com.sentinel.threat.repository.IncidentRepository;
import com.sentinel.threat.repository.RiskScoreRepository;
import com.sentinel.threat.repository.ThreatEventRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ThreatStatisticsService {
    private final ThreatEventRepository threatEventRepository;
    private final IncidentRepository incidentRepository;
    private final RiskScoreRepository riskScoreRepository;

    public ThreatStatisticsService(ThreatEventRepository threatEventRepository,
                                   IncidentRepository incidentRepository,
                                   RiskScoreRepository riskScoreRepository) {
        this.threatEventRepository = threatEventRepository;
        this.incidentRepository = incidentRepository;
        this.riskScoreRepository = riskScoreRepository;
    }

    @Transactional(readOnly = true)
    public ThreatStatisticsResponse getStatistics(Integer hours) {
        int windowHours = (hours != null && hours > 0) ? hours : 24;
        Instant since = Instant.now().minus(windowHours, ChronoUnit.HOURS);

        long totalThreatCount = threatEventRepository.countByCreatedAtAfter(since);
        long criticalIncidentCount = incidentRepository.countBySeverityAndStatus(ThreatSeverity.CRITICAL, IncidentStatus.OPEN);
        Double avgRisk = riskScoreRepository.getAverageRiskScoreSince(since);

        List<Map<String, Object>> topThreatTypes = mapQueryResults(threatEventRepository.findTopThreatTypesSince(since, PageRequest.of(0, 5)), "type", "count");
        List<Map<String, Object>> topAttackingIps = mapQueryResults(threatEventRepository.findTopAttackingIpsSince(since, PageRequest.of(0, 5)), "ip", "count");
        List<Map<String, Object>> topTargetedEndpoints = mapQueryResults(threatEventRepository.findTopTargetedEndpointsSince(since, PageRequest.of(0, 5)), "endpoint", "count");

        return new ThreatStatisticsResponse(
                totalThreatCount,
                criticalIncidentCount,
                avgRisk != null ? Math.round(avgRisk * 10.0) / 10.0 : 0.0,
                topThreatTypes,
                topAttackingIps,
                topTargetedEndpoints
        );
    }

    private List<Map<String, Object>> mapQueryResults(List<Object[]> queryResults, String keyLabel, String valueLabel) {
        return queryResults.stream()
                .map(row -> Map.of(keyLabel, row[0] != null ? row[0].toString() : "UNKNOWN", valueLabel, row[1]))
                .toList();
    }
}
