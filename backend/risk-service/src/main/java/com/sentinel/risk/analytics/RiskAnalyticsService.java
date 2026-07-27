package com.sentinel.risk.analytics;

import com.sentinel.risk.domain.entity.RiskHistoryEntity;
import com.sentinel.risk.dto.RiskTrendResponse;
import com.sentinel.risk.repository.RiskHistoryRepository;
import com.sentinel.risk.repository.RiskScoreRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RiskAnalyticsService {
    private final RiskScoreRepository riskScoreRepository;
    private final RiskHistoryRepository historyRepository;

    public RiskAnalyticsService(RiskScoreRepository riskScoreRepository, RiskHistoryRepository historyRepository) {
        this.riskScoreRepository = riskScoreRepository;
        this.historyRepository = historyRepository;
    }

    @Transactional(readOnly = true)
    public RiskTrendResponse getRiskTrends(Integer hours) {
        int window = (hours != null && hours > 0) ? hours : 24;
        Instant since = Instant.now().minus(window, ChronoUnit.HOURS);

        Double avgRisk = riskScoreRepository.getAverageRiskScoreSince(since);
        List<Object[]> distribution = riskScoreRepository.findRiskDistributionSince(since);

        List<Map<String, Object>> dataPoints = distribution.stream()
                .map(row -> Map.of("classification", row[0].toString(), "count", row[1]))
                .toList();

        return new RiskTrendResponse(
                window + "h",
                avgRisk != null ? Math.round(avgRisk * 10.0) / 10.0 : 0.0,
                dataPoints
        );
    }

    @Transactional(readOnly = true)
    public List<RiskHistoryEntity> getRecentHistory() {
        return historyRepository.findTop50ByOrderByTimestampDesc();
    }
}
