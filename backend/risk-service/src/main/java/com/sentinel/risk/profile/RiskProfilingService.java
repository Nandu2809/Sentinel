package com.sentinel.risk.profile;

import com.sentinel.risk.domain.entity.EndpointRiskEntity;
import com.sentinel.risk.domain.entity.IpRiskEntity;
import com.sentinel.risk.domain.entity.RiskHistoryEntity;
import com.sentinel.risk.domain.entity.RiskProfileEntity;
import com.sentinel.risk.domain.entity.UserRiskEntity;
import com.sentinel.risk.domain.model.EntityType;
import com.sentinel.risk.dto.CalculateRiskRequest;
import com.sentinel.risk.events.RiskEventPublisher;
import com.sentinel.risk.events.RiskThresholdExceededEvent;
import com.sentinel.risk.events.RiskUpdatedEvent;
import com.sentinel.risk.repository.EndpointRiskRepository;
import com.sentinel.risk.repository.IpRiskRepository;
import com.sentinel.risk.repository.RiskHistoryRepository;
import com.sentinel.risk.repository.RiskProfileRepository;
import com.sentinel.risk.repository.UserRiskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RiskProfilingService {
    private final RiskProfileRepository profileRepository;
    private final RiskHistoryRepository historyRepository;
    private final UserRiskRepository userRiskRepository;
    private final IpRiskRepository ipRiskRepository;
    private final EndpointRiskRepository endpointRiskRepository;
    private final RiskEventPublisher eventPublisher;

    public RiskProfilingService(RiskProfileRepository profileRepository,
                                 RiskHistoryRepository historyRepository,
                                 UserRiskRepository userRiskRepository,
                                 IpRiskRepository ipRiskRepository,
                                 EndpointRiskRepository endpointRiskRepository,
                                 RiskEventPublisher eventPublisher) {
        this.profileRepository = profileRepository;
        this.historyRepository = historyRepository;
        this.userRiskRepository = userRiskRepository;
        this.ipRiskRepository = ipRiskRepository;
        this.endpointRiskRepository = endpointRiskRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public RiskProfileEntity updateEntityProfile(CalculateRiskRequest request, double newScore) {
        RiskProfileEntity profile = profileRepository.findByEntityTypeAndEntityId(request.entityType(), request.entityId())
                .orElseGet(() -> new RiskProfileEntity(request.entityType(), request.entityId(), newScore));

        double previousScore = profile.getCurrentRiskScore();
        boolean isThreat = request.severity() != null;
        boolean isIncident = "CRITICAL".equalsIgnoreCase(request.severity()) || newScore >= 80.0;

        profile.updateScore(newScore, isThreat, isIncident);
        RiskProfileEntity saved = profileRepository.save(profile);

        historyRepository.save(new RiskHistoryEntity(
                request.entityType(), request.entityId(), previousScore, newScore, "Telemetry Risk Assessment"
        ));

        updateSpecificEntityRepository(request, newScore);

        eventPublisher.publishRiskUpdated(new RiskUpdatedEvent(
                request.entityType(), request.entityId(), previousScore, newScore, saved.getRiskTrend().name()
        ));

        if (newScore >= 75.0 && previousScore < 75.0) {
            eventPublisher.publishRiskThresholdExceeded(new RiskThresholdExceededEvent(
                    request.entityType(), request.entityId(), newScore, 75.0
            ));
        }

        return saved;
    }

    private void updateSpecificEntityRepository(CalculateRiskRequest request, double newScore) {
        if (request.entityType() == EntityType.USER) {
            userRiskRepository.findByUserId(request.entityId())
                    .ifPresentOrElse(
                            u -> u.updateRisk(newScore, Boolean.TRUE.equals(request.isAuthFailure())),
                            () -> userRiskRepository.save(new UserRiskEntity(request.entityId(), newScore, Boolean.TRUE.equals(request.isAuthFailure()) ? 1 : 0, "STANDARD"))
                    );
        } else if (request.entityType() == EntityType.CLIENT_IP) {
            ipRiskRepository.findByClientIp(request.entityId())
                    .ifPresentOrElse(
                            ip -> ip.updateRisk(newScore, Boolean.TRUE.equals(request.isKnownBot())),
                            () -> ipRiskRepository.save(new IpRiskEntity(request.entityId(), newScore, "NEUTRAL", "UNKNOWN", Boolean.TRUE.equals(request.isKnownBot())))
                    );
        } else if (request.entityType() == EntityType.ENDPOINT) {
            endpointRiskRepository.findByEndpoint(request.entityId())
                    .ifPresentOrElse(
                            ep -> ep.updateRisk(newScore, request.severity() != null),
                            () -> endpointRiskRepository.save(new EndpointRiskEntity(request.entityId(), newScore, Boolean.TRUE.equals(request.isCriticalAsset()), 1, request.severity() != null ? 1 : 0))
                    );
        }
    }
}
