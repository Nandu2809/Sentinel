package com.sentinel.monitoring.service;

import com.sentinel.monitoring.api.dto.ApiMetricsSummaryResponse;
import com.sentinel.monitoring.repository.ApiRequestRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MetricsAggregationService {
    private final ApiRequestRepository requestRepository;

    public MetricsAggregationService(ApiRequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    @Transactional(readOnly = true)
    public ApiMetricsSummaryResponse getMetricsSummary(Integer hours) {
        int durationHours = (hours != null && hours > 0) ? hours : 24;
        Instant since = Instant.now().minus(durationHours, ChronoUnit.HOURS);

        long totalRequests = requestRepository.countRequestsSince(since);
        Double avgLatency = requestRepository.getAverageLatencySince(since);

        List<Map<String, Object>> topUsed = mapResults(requestRepository.findTopUsedApisSince(since, PageRequest.of(0, 5)), "requestCount");
        List<Map<String, Object>> slowest = mapResults(requestRepository.findSlowestApisSince(since, PageRequest.of(0, 5)), "avgLatencyMs");
        List<Map<String, Object>> highestErrors = mapResults(requestRepository.findHighestErrorRateApisSince(since, PageRequest.of(0, 5)), "errorCount");

        return new ApiMetricsSummaryResponse(
                totalRequests,
                avgLatency != null ? Math.round(avgLatency * 100.0) / 100.0 : 0.0,
                topUsed,
                slowest,
                highestErrors
        );
    }

    private List<Map<String, Object>> mapResults(List<Object[]> queryResults, String metricName) {
        return queryResults.stream()
                .map(row -> Map.of("apiName", row[0] != null ? row[0] : "UNREGISTERED", metricName, row[1]))
                .toList();
    }
}
