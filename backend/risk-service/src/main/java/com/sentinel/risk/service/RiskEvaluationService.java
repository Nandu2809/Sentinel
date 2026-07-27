package com.sentinel.risk.service;

import com.sentinel.risk.domain.entity.RiskProfileEntity;
import com.sentinel.risk.domain.entity.RiskScoreEntity;
import com.sentinel.risk.dto.CalculateRiskRequest;
import com.sentinel.risk.dto.RiskProfileResponse;
import com.sentinel.risk.dto.RiskScoreResponse;
import com.sentinel.risk.events.RiskCalculatedEvent;
import com.sentinel.risk.events.RiskEventPublisher;
import com.sentinel.risk.mapper.RiskMapper;
import com.sentinel.risk.profile.RiskProfilingService;
import com.sentinel.risk.repository.RiskProfileRepository;
import com.sentinel.risk.repository.RiskScoreRepository;
import com.sentinel.risk.scoring.RiskCalculationStrategy;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RiskEvaluationService {
    private final RiskCalculationStrategy calculationStrategy;
    private final RiskProfilingService profilingService;
    private final RiskScoreRepository scoreRepository;
    private final RiskProfileRepository profileRepository;
    private final RiskMapper mapper;
    private final RiskEventPublisher eventPublisher;

    public RiskEvaluationService(RiskCalculationStrategy calculationStrategy,
                                 RiskProfilingService profilingService,
                                 RiskScoreRepository scoreRepository,
                                 RiskProfileRepository profileRepository,
                                 RiskMapper mapper,
                                 RiskEventPublisher eventPublisher) {
        this.calculationStrategy = calculationStrategy;
        this.profilingService = profilingService;
        this.scoreRepository = scoreRepository;
        this.profileRepository = profileRepository;
        this.mapper = mapper;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public RiskScoreResponse evaluateRisk(CalculateRiskRequest request) {
        double score = calculationStrategy.calculateScore(request);

        String factors = String.format("{\"severity\":\"%s\",\"isAuthFailure\":%b,\"isCriticalAsset\":%b,\"isKnownBot\":%b}",
                request.severity(), Boolean.TRUE.equals(request.isAuthFailure()),
                Boolean.TRUE.equals(request.isCriticalAsset()), Boolean.TRUE.equals(request.isKnownBot()));

        RiskScoreEntity scoreEntity = scoreRepository.save(new RiskScoreEntity(
                request.entityType(), request.entityId(), score, factors
        ));

        profilingService.updateEntityProfile(request, score);

        eventPublisher.publishRiskCalculated(new RiskCalculatedEvent(
                request.entityType(), request.entityId(), score, scoreEntity.getClassification().name()
        ));

        return mapper.toRiskScoreResponse(scoreEntity);
    }

    @Transactional(readOnly = true)
    public List<RiskProfileResponse> getAllProfiles() {
        return profileRepository.findAll().stream()
                .map(mapper::toRiskProfileResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<RiskScoreResponse> getRiskScoresForEntity(CalculateRiskRequest request, Pageable pageable) {
        return scoreRepository.findByEntityTypeAndEntityIdOrderByCalculatedAtDesc(request.entityType(), request.entityId(), pageable)
                .map(mapper::toRiskScoreResponse);
    }
}
