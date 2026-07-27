package com.sentinel.risk.intelligence;

import com.sentinel.risk.domain.entity.OrganizationSecurityScoreEntity;
import com.sentinel.risk.dto.SecurityScoreResponse;
import com.sentinel.risk.events.RiskEventPublisher;
import com.sentinel.risk.events.SecurityScoreUpdatedEvent;
import com.sentinel.risk.mapper.RiskMapper;
import com.sentinel.risk.repository.OrganizationSecurityScoreRepository;
import com.sentinel.risk.repository.RiskProfileRepository;
import com.sentinel.risk.repository.RiskScoreRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrganizationScoreService {
    private final OrganizationSecurityScoreRepository organizationScoreRepository;
    private final RiskScoreRepository riskScoreRepository;
    private final RiskProfileRepository profileRepository;
    private final RiskMapper mapper;
    private final RiskEventPublisher eventPublisher;

    public OrganizationScoreService(OrganizationSecurityScoreRepository organizationScoreRepository,
                                     RiskScoreRepository riskScoreRepository,
                                     RiskProfileRepository profileRepository,
                                     RiskMapper mapper,
                                     RiskEventPublisher eventPublisher) {
        this.organizationScoreRepository = organizationScoreRepository;
        this.riskScoreRepository = riskScoreRepository;
        this.profileRepository = profileRepository;
        this.mapper = mapper;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public SecurityScoreResponse calculateAndRecordOrganizationScore() {
        Instant since = Instant.now().minus(24, ChronoUnit.HOURS);
        Double avgRisk = riskScoreRepository.getAverageRiskScoreSince(since);
        double averageRiskScore = avgRisk != null ? avgRisk : 0.0;

        long highRiskEntities = profileRepository.countEntitiesAtHighOrCriticalRisk();
        double threatHeatIndex = Math.min(Math.round((averageRiskScore * 0.7 + highRiskEntities * 5.0) * 10.0) / 10.0, 100.0);

        double rawScore = 100.0 - (averageRiskScore * 0.4) - (highRiskEntities * 3.0);
        double securityScore = Math.min(Math.max(Math.round(rawScore * 10.0) / 10.0, 0.0), 100.0);

        OrganizationSecurityScoreEntity entity = new OrganizationSecurityScoreEntity(
                securityScore, threatHeatIndex, 0, highRiskEntities
        );

        OrganizationSecurityScoreEntity saved = organizationScoreRepository.save(entity);

        eventPublisher.publishSecurityScoreUpdated(new SecurityScoreUpdatedEvent(
                saved.getSecurityScore(), saved.getThreatHeatIndex(), saved.getActiveCriticalIncidents()
        ));

        return mapper.toSecurityScoreResponse(saved);
    }

    @Transactional(readOnly = true)
    public SecurityScoreResponse getLatestOrganizationScore() {
        return organizationScoreRepository.findTopByOrderByCalculatedAtDesc()
                .map(mapper::toSecurityScoreResponse)
                .orElseGet(() -> new SecurityScoreResponse(100.0, 0.0, 0, 0, Instant.now()));
    }
}
