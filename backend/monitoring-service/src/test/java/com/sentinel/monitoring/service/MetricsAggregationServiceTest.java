package com.sentinel.monitoring.service;

import com.sentinel.monitoring.api.dto.ApiMetricsSummaryResponse;
import com.sentinel.monitoring.repository.ApiRequestRepository;
import java.time.Instant;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MetricsAggregationServiceTest {

    @Mock
    private ApiRequestRepository requestRepository;

    private MetricsAggregationService metricsAggregationService;

    @BeforeEach
    void setUp() {
        metricsAggregationService = new MetricsAggregationService(requestRepository);
    }

    @Test
    void getMetricsSummary_Success() {
        when(requestRepository.countRequestsSince(any(Instant.class))).thenReturn(150L);
        when(requestRepository.getAverageLatencySince(any(Instant.class))).thenReturn(45.678);
        when(requestRepository.findTopUsedApisSince(any(Instant.class), any())).thenReturn(Collections.emptyList());
        when(requestRepository.findSlowestApisSince(any(Instant.class), any())).thenReturn(Collections.emptyList());
        when(requestRepository.findHighestErrorRateApisSince(any(Instant.class), any())).thenReturn(Collections.emptyList());

        ApiMetricsSummaryResponse summary = metricsAggregationService.getMetricsSummary(24);

        assertNotNull(summary);
        assertEquals(150L, summary.totalRequests());
        assertEquals(45.68, summary.averageLatencyMs());
    }
}
