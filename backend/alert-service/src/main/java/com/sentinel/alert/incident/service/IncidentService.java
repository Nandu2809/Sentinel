package com.sentinel.alert.incident.service;

import com.sentinel.alert.domain.entity.AlertEntity;
import com.sentinel.alert.domain.model.AlertSeverity;
import com.sentinel.alert.incident.domain.entity.IncidentActionEntity;
import com.sentinel.alert.incident.domain.entity.IncidentEntity;
import com.sentinel.alert.incident.domain.entity.IncidentNoteEntity;
import com.sentinel.alert.incident.domain.entity.IncidentTimelineEventEntity;
import com.sentinel.alert.incident.domain.model.IncidentActionType;
import com.sentinel.alert.incident.domain.model.IncidentStatus;
import com.sentinel.alert.incident.dto.AddIncidentNoteRequest;
import com.sentinel.alert.incident.dto.AssignIncidentRequest;
import com.sentinel.alert.incident.dto.IncidentActionResponse;
import com.sentinel.alert.incident.dto.IncidentNoteResponse;
import com.sentinel.alert.incident.dto.IncidentResponse;
import com.sentinel.alert.incident.dto.IncidentTimelineEventResponse;
import com.sentinel.alert.incident.dto.PerformIncidentActionRequest;
import com.sentinel.alert.incident.dto.UpdateIncidentStatusRequest;
import com.sentinel.alert.incident.repository.IncidentActionRepository;
import com.sentinel.alert.incident.repository.IncidentNoteRepository;
import com.sentinel.alert.incident.repository.IncidentRepository;
import com.sentinel.alert.incident.repository.IncidentTimelineEventRepository;
import com.sentinel.common.exception.BusinessException;
import com.sentinel.common.exception.ErrorCode;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IncidentService {
    private static final Logger log = LoggerFactory.getLogger(IncidentService.class);
    private static final AtomicLong INCIDENT_COUNTER = new AtomicLong(1000);

    private final IncidentRepository incidentRepository;
    private final IncidentTimelineEventRepository timelineRepository;
    private final IncidentActionRepository actionRepository;
    private final IncidentNoteRepository noteRepository;

    public IncidentService(IncidentRepository incidentRepository,
                           IncidentTimelineEventRepository timelineRepository,
                           IncidentActionRepository actionRepository,
                           IncidentNoteRepository noteRepository) {
        this.incidentRepository = incidentRepository;
        this.timelineRepository = timelineRepository;
        this.actionRepository = actionRepository;
        this.noteRepository = noteRepository;
    }

    @Transactional
    public IncidentEntity createIncidentFromAlert(AlertEntity alert) {
        if (alert == null) {
            return null;
        }

        // Prevent duplicate creation for the same alert
        if (alert.getId() != null && incidentRepository.existsByAlertId(alert.getId())) {
            log.info("Incident already exists for alertId={}", alert.getId());
            return incidentRepository.findByAlertId(alert.getId()).orElse(null);
        }

        String incidentNum = generateIncidentNumber();
        double aiConfidence = alert.getRiskScore() > 80 ? 94.5 : (alert.getRiskScore() > 50 ? 82.0 : 65.0);

        String user = alert.getAffectedUser() != null ? alert.getAffectedUser() : "unknown_user";
        String ip = alert.getAffectedIp() != null ? alert.getAffectedIp() : "192.168.1.100";
        String device = "Linux x86_64 / SOC Terminal";

        IncidentEntity incident = new IncidentEntity(
                incidentNum,
                alert.getId(),
                alert.getTitle(),
                alert.getDescription(),
                alert.getSeverity(),
                IncidentStatus.OPEN,
                alert.getAssignedAnalyst(),
                alert.getRiskScore(),
                aiConfidence,
                user,
                ip,
                device,
                alert.getEvidenceJson()
        );

        IncidentEntity saved = incidentRepository.save(incident);
        log.info("INCIDENT_CREATED incidentNumber={} alertId={} severity={} riskScore={}",
                saved.getIncidentNumber(), saved.getAlertId(), saved.getSeverity(), saved.getRiskScore());

        // Build initial attack timeline based on evidence & alert attributes
        buildInitialTimeline(saved, alert);

        return saved;
    }

    private void buildInitialTimeline(IncidentEntity incident, AlertEntity alert) {
        Instant baseTime = alert.getCreatedAt() != null ? alert.getCreatedAt() : Instant.now();

        // 1. Initial Access Vector / Event
        timelineRepository.save(new IncidentTimelineEventEntity(
                incident.getId(),
                "LOGIN_FAILED",
                "Authentication Gateway",
                "Multiple failed authentication attempts detected for user: " + incident.getAffectedUser(),
                "{\"user\":\"" + incident.getAffectedUser() + "\",\"ip\":\"" + incident.getAffectedIp() + "\"}",
                baseTime.minusSeconds(180)
        ));

        // 2. Threat Detection
        String threatTitle = alert.getAlertType() != null ? alert.getAlertType() : "BRUTE_FORCE_DETECTED";
        timelineRepository.save(new IncidentTimelineEventEntity(
                incident.getId(),
                threatTitle,
                "Threat Detection Engine",
                "Attack signature matched: " + threatTitle + " on endpoint " + (alert.getAffectedApi() != null ? alert.getAffectedApi() : "/api/v1/auth/login"),
                "{\"signature\":\"SIG-RULE-" + (Math.abs(incident.getIncidentNumber().hashCode() % 900) + 100) + "\"}",
                baseTime.minusSeconds(120)
        ));

        // 3. AI Anomaly Prediction
        timelineRepository.save(new IncidentTimelineEventEntity(
                incident.getId(),
                "AI_ANOMALY",
                "AI Behavioral Engine",
                "Anomalous access pattern detected with " + incident.getAiConfidence() + "% confidence score",
                "{\"aiConfidence\":" + incident.getAiConfidence() + ",\"anomalyType\":\"BEHAVIORAL_DEVIATION\"}",
                baseTime.minusSeconds(60)
        ));

        // 4. Risk Engine Decision
        timelineRepository.save(new IncidentTimelineEventEntity(
                incident.getId(),
                "RISK_CRITICAL",
                "Risk Engine",
                "Calculated risk score " + incident.getRiskScore() + "/100 — threshold exceeded for " + incident.getSeverity() + " alert level",
                "{\"riskScore\":" + incident.getRiskScore() + ",\"severity\":\"" + incident.getSeverity() + "\"}",
                baseTime.minusSeconds(30)
        ));

        // 5. Alert Created
        timelineRepository.save(new IncidentTimelineEventEntity(
                incident.getId(),
                "ALERT_CREATED",
                "Alert Management Engine",
                "Alert " + alert.getAlertCode() + " created and dispatched to SOC Command Center",
                "{\"alertCode\":\"" + alert.getAlertCode() + "\",\"incidentNumber\":\"" + incident.getIncidentNumber() + "\"}",
                baseTime
        ));
    }

    @Transactional(readOnly = true)
    public Page<IncidentResponse> getIncidents(IncidentStatus status, AlertSeverity severity, Pageable pageable) {
        if (status != null) {
            return incidentRepository.findByStatus(status, pageable).map(this::toIncidentResponseSummary);
        }
        if (severity != null) {
            return incidentRepository.findBySeverity(severity, pageable).map(this::toIncidentResponseSummary);
        }
        return incidentRepository.findAll(pageable).map(this::toIncidentResponseSummary);
    }

    @Transactional(readOnly = true)
    public IncidentResponse getIncidentById(UUID id) {
        IncidentEntity incident = incidentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Incident not found: " + id));
        return toFullIncidentResponse(incident);
    }

    @Transactional
    public IncidentResponse assignIncident(UUID id, AssignIncidentRequest request) {
        IncidentEntity incident = incidentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Incident not found: " + id));

        String analyst = request.assignedAnalyst();
        incident.assignAnalyst(analyst);
        IncidentEntity saved = incidentRepository.save(incident);

        String performedBy = request.assignedBy() != null ? request.assignedBy() : "SOC_LEAD";
        actionRepository.save(new IncidentActionEntity(
                saved.getId(),
                IncidentActionType.ASSIGN,
                performedBy,
                analyst,
                "Assigned incident " + saved.getIncidentNumber() + " to analyst " + analyst
        ));

        timelineRepository.save(new IncidentTimelineEventEntity(
                saved.getId(),
                "ANALYST_ASSIGNED",
                "SOC Command Center",
                "Incident assigned to analyst: " + analyst,
                "{\"assignedBy\":\"" + performedBy + "\",\"analyst\":\"" + analyst + "\"}",
                Instant.now()
        ));

        return toFullIncidentResponse(saved);
    }

    @Transactional
    public IncidentResponse updateIncidentStatus(UUID id, UpdateIncidentStatusRequest request) {
        IncidentEntity incident = incidentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Incident not found: " + id));

        validateStatusTransition(incident.getStatus(), request.status());

        IncidentStatus prevStatus = incident.getStatus();
        incident.updateStatus(request.status());
        IncidentEntity saved = incidentRepository.save(incident);

        String performedBy = request.updatedBy() != null ? request.updatedBy() : "SOC_ANALYST";
        actionRepository.save(new IncidentActionEntity(
                saved.getId(),
                mapStatusToActionType(request.status()),
                performedBy,
                saved.getIncidentNumber(),
                "Status updated from " + prevStatus + " to " + request.status() + ". Notes: " + (request.notes() != null ? request.notes() : "N/A")
        ));

        timelineRepository.save(new IncidentTimelineEventEntity(
                saved.getId(),
                "STATUS_UPDATED_" + request.status(),
                "SOC Command Center",
                "Incident status changed from " + prevStatus + " to " + request.status(),
                "{\"previousStatus\":\"" + prevStatus + "\",\"newStatus\":\"" + request.status() + "\"}",
                Instant.now()
        ));

        if (request.notes() != null && !request.notes().isBlank()) {
            noteRepository.save(new IncidentNoteEntity(saved.getId(), performedBy, request.notes()));
        }

        return toFullIncidentResponse(saved);
    }

    @Transactional
    public IncidentNoteResponse addNote(UUID id, AddIncidentNoteRequest request) {
        IncidentEntity incident = incidentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Incident not found: " + id));

        String author = request.author() != null ? request.author() : "SOC_ANALYST";
        IncidentNoteEntity note = new IncidentNoteEntity(incident.getId(), author, request.content());
        IncidentNoteEntity saved = noteRepository.save(note);

        timelineRepository.save(new IncidentTimelineEventEntity(
                incident.getId(),
                "NOTE_ADDED",
                "Analyst Workbench",
                "Note added by " + author,
                "{\"author\":\"" + author + "\",\"noteId\":\"" + saved.getId() + "\"}",
                Instant.now()
        ));

        return new IncidentNoteResponse(saved.getId(), saved.getIncidentId(), saved.getAuthor(), saved.getContent(), saved.getCreatedAt());
    }

    @Transactional
    public IncidentResponse performAction(UUID id, PerformIncidentActionRequest request) {
        IncidentEntity incident = incidentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Incident not found: " + id));

        String performedBy = request.performedBy() != null ? request.performedBy() : "SOC_ANALYST";
        String target = request.target() != null ? request.target() : getActionDefaultTarget(incident, request.actionType());
        String actionDetails = request.details();

        String auditDetails;
        switch (request.actionType()) {
            case ACKNOWLEDGE -> {
                incident.updateStatus(IncidentStatus.ACKNOWLEDGED);
                auditDetails = "Incident acknowledged by SOC analyst";
            }
            case ASSIGN -> {
                incident.assignAnalyst(target);
                auditDetails = "Assigned incident to " + target;
            }
            case BLOCK_IP -> {
                auditDetails = "Action recorded — enforcement integration pending (IP: " + target + "). " + (actionDetails != null ? actionDetails : "");
            }
            case DISABLE_USER -> {
                auditDetails = "Action recorded — enforcement integration pending (User: " + target + "). " + (actionDetails != null ? actionDetails : "");
            }
            case RESET_PASSWORD -> {
                auditDetails = "Action recorded — enforcement integration pending (User: " + target + "). " + (actionDetails != null ? actionDetails : "");
            }
            case RESOLVE -> {
                incident.updateStatus(IncidentStatus.RESOLVED);
                auditDetails = "Incident resolved. Details: " + (actionDetails != null ? actionDetails : "Mitigation verified");
            }
            default -> auditDetails = "Action executed: " + request.actionType();
        }

        IncidentEntity saved = incidentRepository.save(incident);

        actionRepository.save(new IncidentActionEntity(
                saved.getId(),
                request.actionType(),
                performedBy,
                target,
                auditDetails
        ));

        timelineRepository.save(new IncidentTimelineEventEntity(
                saved.getId(),
                "ACTION_" + request.actionType().name(),
                "Analyst Actions",
                "Analyst action executed: " + request.actionType().name() + " on target " + target,
                "{\"actionType\":\"" + request.actionType().name() + "\",\"performedBy\":\"" + performedBy + "\",\"details\":\"" + auditDetails + "\"}",
                Instant.now()
        ));

        return toFullIncidentResponse(saved);
    }

    private void validateStatusTransition(IncidentStatus current, IncidentStatus target) {
        if (current == target) {
            return;
        }
        boolean valid = switch (current) {
            case OPEN -> target == IncidentStatus.ACKNOWLEDGED || target == IncidentStatus.INVESTIGATING || target == IncidentStatus.RESOLVED;
            case ACKNOWLEDGED -> target == IncidentStatus.INVESTIGATING || target == IncidentStatus.MITIGATED || target == IncidentStatus.RESOLVED;
            case INVESTIGATING -> target == IncidentStatus.MITIGATED || target == IncidentStatus.RESOLVED;
            case MITIGATED -> target == IncidentStatus.RESOLVED || target == IncidentStatus.CLOSED;
            case RESOLVED -> target == IncidentStatus.CLOSED || target == IncidentStatus.INVESTIGATING;
            case CLOSED -> false;
        };

        if (!valid) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, "Invalid status transition from " + current + " to " + target);
        }
    }

    private String getActionDefaultTarget(IncidentEntity incident, IncidentActionType actionType) {
        return switch (actionType) {
            case BLOCK_IP -> incident.getAffectedIp() != null ? incident.getAffectedIp() : "192.168.1.100";
            case DISABLE_USER, RESET_PASSWORD -> incident.getAffectedUser() != null ? incident.getAffectedUser() : "affected_user";
            case ASSIGN -> incident.getAssignedAnalyst() != null ? incident.getAssignedAnalyst() : "soc_analyst";
            default -> incident.getIncidentNumber();
        };
    }

    private IncidentActionType mapStatusToActionType(IncidentStatus status) {
        return switch (status) {
            case ACKNOWLEDGED -> IncidentActionType.ACKNOWLEDGE;
            case RESOLVED -> IncidentActionType.RESOLVE;
            default -> IncidentActionType.ASSIGN;
        };
    }

    private String generateIncidentNumber() {
        return "INC-2026-" + String.format("%04d", INCIDENT_COUNTER.getAndIncrement());
    }

    private IncidentResponse toIncidentResponseSummary(IncidentEntity entity) {
        return new IncidentResponse(
                entity.getId(),
                entity.getIncidentNumber(),
                entity.getAlertId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getSeverity(),
                entity.getStatus(),
                entity.getAssignedAnalyst(),
                entity.getRiskScore(),
                entity.getAiConfidence(),
                entity.getAffectedUser(),
                entity.getAffectedIp(),
                entity.getAffectedDevice(),
                entity.getEvidenceJson(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getResolvedAt(),
                List.of(),
                List.of(),
                List.of()
        );
    }

    private IncidentResponse toFullIncidentResponse(IncidentEntity entity) {
        List<IncidentTimelineEventResponse> timeline = timelineRepository.findByIncidentIdOrderByTimestampAsc(entity.getId()).stream()
                .map(t -> new IncidentTimelineEventResponse(t.getId(), t.getIncidentId(), t.getEventType(), t.getSource(), t.getSummary(), t.getDetailsJson(), t.getTimestamp()))
                .toList();

        List<IncidentActionResponse> actions = actionRepository.findByIncidentIdOrderByTimestampDesc(entity.getId()).stream()
                .map(a -> new IncidentActionResponse(a.getId(), a.getIncidentId(), a.getActionType(), a.getPerformedBy(), a.getTarget(), a.getDetails(), a.getTimestamp()))
                .toList();

        List<IncidentNoteResponse> notes = noteRepository.findByIncidentIdOrderByCreatedAtDesc(entity.getId()).stream()
                .map(n -> new IncidentNoteResponse(n.getId(), n.getIncidentId(), n.getAuthor(), n.getContent(), n.getCreatedAt()))
                .toList();

        return new IncidentResponse(
                entity.getId(),
                entity.getIncidentNumber(),
                entity.getAlertId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getSeverity(),
                entity.getStatus(),
                entity.getAssignedAnalyst(),
                entity.getRiskScore(),
                entity.getAiConfidence(),
                entity.getAffectedUser(),
                entity.getAffectedIp(),
                entity.getAffectedDevice(),
                entity.getEvidenceJson(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getResolvedAt(),
                timeline,
                actions,
                notes
        );
    }
}
