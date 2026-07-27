package com.sentinel.threat.service;

import com.sentinel.common.exception.BusinessException;
import com.sentinel.common.exception.ErrorCode;
import com.sentinel.threat.domain.entity.DetectionEntity;
import com.sentinel.threat.domain.entity.ThreatEventEntity;
import com.sentinel.threat.domain.entity.ThreatRuleEntity;
import com.sentinel.threat.domain.model.ThreatSeverity;
import com.sentinel.threat.domain.model.ThreatStatus;
import com.sentinel.threat.domain.model.ThreatType;
import com.sentinel.threat.dto.AnalyzeRequestDto;
import com.sentinel.threat.dto.ThreatEventResponse;
import com.sentinel.threat.events.ThreatDetectedEvent;
import com.sentinel.threat.events.ThreatEventPublisher;
import com.sentinel.threat.incident.IncidentManagerService;
import com.sentinel.threat.mapper.ThreatMapper;
import com.sentinel.threat.repository.DetectionRepository;
import com.sentinel.threat.repository.ThreatEventRepository;
import com.sentinel.threat.repository.ThreatRuleRepository;
import com.sentinel.threat.risk.RiskScoringEngine;
import com.sentinel.threat.ruleengine.DetectorResult;
import com.sentinel.threat.ruleengine.RequestTelemetry;
import com.sentinel.threat.ruleengine.ThreatRuleEvaluatorService;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ThreatAnalysisService {
    private final ThreatRuleEvaluatorService ruleEvaluatorService;
    private final RiskScoringEngine riskScoringEngine;
    private final ThreatEventRepository threatEventRepository;
    private final DetectionRepository detectionRepository;
    private final ThreatRuleRepository ruleRepository;
    private final IncidentManagerService incidentManagerService;
    private final ThreatMapper mapper;
    private final ThreatEventPublisher eventPublisher;

    public ThreatAnalysisService(ThreatRuleEvaluatorService ruleEvaluatorService,
                                 RiskScoringEngine riskScoringEngine,
                                 ThreatEventRepository threatEventRepository,
                                 DetectionRepository detectionRepository,
                                 ThreatRuleRepository ruleRepository,
                                 IncidentManagerService incidentManagerService,
                                 ThreatMapper mapper,
                                 ThreatEventPublisher eventPublisher) {
        this.ruleEvaluatorService = ruleEvaluatorService;
        this.riskScoringEngine = riskScoringEngine;
        this.threatEventRepository = threatEventRepository;
        this.detectionRepository = detectionRepository;
        this.ruleRepository = ruleRepository;
        this.incidentManagerService = incidentManagerService;
        this.mapper = mapper;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public List<ThreatEventResponse> analyzeRequest(AnalyzeRequestDto dto) {
        RequestTelemetry telemetry = new RequestTelemetry(
                dto.requestId(), dto.correlationId(), dto.clientIp(), dto.userAgent(),
                dto.httpMethod(), dto.uri(), dto.headers(), dto.queryParams(),
                dto.body(), dto.authToken(), dto.userId(), dto.statusCode(),
                dto.latencyMs(), dto.timestamp() != null ? dto.timestamp() : Instant.now()
        );

        List<DetectorResult> matches = ruleEvaluatorService.evaluate(telemetry);
        if (matches.isEmpty()) {
            return List.of();
        }

        ThreatSeverity highestSeverity = matches.stream()
                .map(DetectorResult::severity)
                .max(Enum::compareTo)
                .orElse(ThreatSeverity.LOW);

        double calculatedRiskScore = riskScoringEngine.calculateAndRecordRiskScore(telemetry, matches, highestSeverity);
        ThreatSeverity finalSeverity = riskScoringEngine.mapRiskToSeverity(calculatedRiskScore);

        List<ThreatEventResponse> responses = new ArrayList<>();
        for (DetectorResult match : matches) {
            ThreatRuleEntity matchedRule = ruleRepository.findByRuleCode(match.ruleCode()).orElse(null);
            String threatCode = "THR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

            ThreatEventEntity threatEvent = new ThreatEventEntity(
                    threatCode,
                    telemetry.correlationId(),
                    telemetry.requestId(),
                    telemetry.userId(),
                    telemetry.clientIp(),
                    telemetry.uri(),
                    telemetry.httpMethod(),
                    match.threatType(),
                    matchedRule,
                    finalSeverity,
                    calculatedRiskScore,
                    match.recommendation(),
                    ThreatStatus.NEW,
                    telemetry.timestamp()
            );

            ThreatEventEntity savedEvent = threatEventRepository.save(threatEvent);

            detectionRepository.save(new DetectionEntity(
                    savedEvent, matchedRule, match.ruleCode(), match.matchedPattern(), match.rawPayloadSample()
            ));

            incidentManagerService.processThreatForIncident(savedEvent);

            eventPublisher.publishThreatDetected(new ThreatDetectedEvent(
                    savedEvent.getThreatCode(),
                    savedEvent.getCorrelationId(),
                    savedEvent.getRequestId(),
                    savedEvent.getClientIp(),
                    savedEvent.getEndpoint(),
                    savedEvent.getThreatType(),
                    savedEvent.getSeverity(),
                    savedEvent.getRiskScore(),
                    Instant.now()
            ));

            responses.add(mapper.toThreatEventResponse(savedEvent));
        }

        return responses;
    }

    @Transactional(readOnly = true)
    public Page<ThreatEventResponse> searchThreats(ThreatType threatType, ThreatSeverity severity, String clientIp,
                                                  Instant startTime, Instant endTime, Pageable pageable) {
        return threatEventRepository.searchThreats(threatType, severity, clientIp, startTime, endTime, pageable)
                .map(mapper::toThreatEventResponse);
    }

    @Transactional(readOnly = true)
    public ThreatEventResponse getThreat(UUID id) {
        ThreatEventEntity entity = threatEventRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Threat event not found"));
        return mapper.toThreatEventResponse(entity);
    }
}
