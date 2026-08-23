package com.sentinel.alert.incident.service;

import com.sentinel.alert.domain.entity.AlertEntity;
import com.sentinel.alert.domain.model.AlertSeverity;
import com.sentinel.alert.incident.domain.entity.IncidentEntity;
import com.sentinel.alert.incident.domain.model.IncidentActionType;
import com.sentinel.alert.incident.domain.model.IncidentStatus;
import com.sentinel.alert.incident.dto.AddIncidentNoteRequest;
import com.sentinel.alert.incident.dto.AssignIncidentRequest;
import com.sentinel.alert.incident.dto.IncidentResponse;
import com.sentinel.alert.incident.dto.PerformIncidentActionRequest;
import com.sentinel.alert.incident.dto.UpdateIncidentStatusRequest;
import com.sentinel.alert.incident.repository.IncidentActionRepository;
import com.sentinel.alert.incident.repository.IncidentNoteRepository;
import com.sentinel.alert.incident.repository.IncidentRepository;
import com.sentinel.alert.incident.repository.IncidentTimelineEventRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IncidentServiceTest {

    @Mock private IncidentRepository incidentRepository;
    @Mock private IncidentTimelineEventRepository timelineRepository;
    @Mock private IncidentActionRepository actionRepository;
    @Mock private IncidentNoteRepository noteRepository;

    private IncidentService incidentService;

    @BeforeEach
    void setUp() {
        incidentService = new IncidentService(incidentRepository, timelineRepository, actionRepository, noteRepository);
    }

    @Test
    void createIncidentFromAlert_SuccessfulCreation() {
        AlertEntity alert = new AlertEntity(
                "ALT-TEST-100", "Critical SQL Injection", "Exploit attempt", "SQL_INJECTION",
                AlertSeverity.CRITICAL, 95.0, "threat-service", "/api/v1/query", "admin", "192.168.1.50", "corr-100", "{}"
        );

        when(incidentRepository.existsByAlertId(alert.getId())).thenReturn(false);
        when(incidentRepository.save(any(IncidentEntity.class))).thenAnswer(i -> i.getArgument(0));

        IncidentEntity incident = incidentService.createIncidentFromAlert(alert);

        assertNotNull(incident);
        assertTrue(incident.getIncidentNumber().startsWith("INC-2026-"));
        assertEquals(AlertSeverity.CRITICAL, incident.getSeverity());
        assertEquals(IncidentStatus.OPEN, incident.getStatus());
        verify(incidentRepository, times(1)).save(any());
        verify(timelineRepository, times(5)).save(any());
    }

    @Test
    void assignIncident_UpdatesAssignedAnalyst() {
        UUID id = UUID.randomUUID();
        IncidentEntity incident = new IncidentEntity(
                "INC-2026-1001", UUID.randomUUID(), "Brute Force Attack", "Details",
                AlertSeverity.HIGH, IncidentStatus.OPEN, null, 88.0, 90.0, "user1", "10.0.0.1", "Linux", "{}"
        );

        when(incidentRepository.findById(id)).thenReturn(Optional.of(incident));
        when(incidentRepository.save(any(IncidentEntity.class))).thenAnswer(i -> i.getArgument(0));

        AssignIncidentRequest request = new AssignIncidentRequest("analyst_jane", "lead_john");
        IncidentResponse res = incidentService.assignIncident(id, request);

        assertNotNull(res);
        assertEquals("analyst_jane", res.assignedAnalyst());
        assertEquals(IncidentStatus.ACKNOWLEDGED, res.status());
    }

    @Test
    void performAction_BlockIp_RecordsAuditAction() {
        UUID id = UUID.randomUUID();
        IncidentEntity incident = new IncidentEntity(
                "INC-2026-1002", UUID.randomUUID(), "Impossible Travel", "NYC to Tokyo",
                AlertSeverity.CRITICAL, IncidentStatus.OPEN, "analyst_jane", 99.0, 96.0, "user2", "203.0.113.5", "Windows", "{}"
        );

        when(incidentRepository.findById(id)).thenReturn(Optional.of(incident));
        when(incidentRepository.save(any(IncidentEntity.class))).thenAnswer(i -> i.getArgument(0));

        PerformIncidentActionRequest req = new PerformIncidentActionRequest(
                IncidentActionType.BLOCK_IP, "analyst_jane", "203.0.113.5", "Block attacker IP at edge firewall"
        );

        IncidentResponse res = incidentService.performAction(id, req);

        assertNotNull(res);
        verify(actionRepository, times(1)).save(any());
        verify(timelineRepository, times(1)).save(any());
    }
}
