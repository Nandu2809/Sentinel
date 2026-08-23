package com.sentinel.alert.incident.service;

import com.sentinel.alert.domain.entity.AlertEntity;
import com.sentinel.alert.incident.domain.entity.IncidentEntity;
import com.sentinel.alert.incident.dto.ThreatHuntingQueryRequest;
import com.sentinel.alert.incident.dto.ThreatHuntingSearchResponse;
import com.sentinel.alert.incident.repository.IncidentRepository;
import com.sentinel.alert.repository.AlertRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ThreatHuntingService {
    private final IncidentRepository incidentRepository;
    private final AlertRepository alertRepository;

    public ThreatHuntingService(IncidentRepository incidentRepository, AlertRepository alertRepository) {
        this.incidentRepository = incidentRepository;
        this.alertRepository = alertRepository;
    }

    @Transactional(readOnly = true)
    public List<ThreatHuntingSearchResponse> search(ThreatHuntingQueryRequest query) {
        List<ThreatHuntingSearchResponse> results = new ArrayList<>();

        List<IncidentEntity> matchingIncidents = incidentRepository.searchIncidents(
                query.username(),
                query.ipAddress(),
                query.device(),
                query.eventType(),
                query.minRiskScore(),
                query.maxRiskScore()
        );

        for (IncidentEntity inc : matchingIncidents) {
            results.add(new ThreatHuntingSearchResponse(
                    inc.getId(),
                    inc.getIncidentNumber(),
                    inc.getTitle(),
                    inc.getTitle(),
                    "Incident Engine",
                    inc.getAffectedUser(),
                    inc.getAffectedIp(),
                    inc.getAffectedDevice(),
                    inc.getRiskScore(),
                    inc.getSeverity(),
                    inc.getCreatedAt(),
                    inc.getId()
            ));
        }

        // If no incident results matched yet, fallback to searching alerts table
        if (results.isEmpty()) {
            List<AlertEntity> alerts = alertRepository.findAll();
            for (AlertEntity alt : alerts) {
                boolean matchesUser = query.username() == null || (alt.getAffectedUser() != null && alt.getAffectedUser().toLowerCase().contains(query.username().toLowerCase()));
                boolean matchesIp = query.ipAddress() == null || (alt.getAffectedIp() != null && alt.getAffectedIp().toLowerCase().contains(query.ipAddress().toLowerCase()));
                boolean matchesType = query.eventType() == null || (alt.getAlertType() != null && alt.getAlertType().toLowerCase().contains(query.eventType().toLowerCase()));
                boolean matchesRisk = (query.minRiskScore() == null || alt.getRiskScore() >= query.minRiskScore()) &&
                                     (query.maxRiskScore() == null || alt.getRiskScore() <= query.maxRiskScore());

                if (matchesUser && matchesIp && matchesType && matchesRisk) {
                    results.add(new ThreatHuntingSearchResponse(
                            alt.getId(),
                            alt.getAlertCode(),
                            alt.getTitle(),
                            alt.getAlertType(),
                            alt.getSourceService(),
                            alt.getAffectedUser(),
                            alt.getAffectedIp(),
                            "Standard Client",
                            alt.getRiskScore(),
                            alt.getSeverity(),
                            alt.getCreatedAt(),
                            null
                    ));
                }
            }
        }

        return results;
    }
}
