package com.sentinel.alert.service;

import com.sentinel.alert.domain.entity.AlertEntity;
import com.sentinel.alert.domain.model.AlertSeverity;
import com.sentinel.alert.domain.model.AlertStatus;
import com.sentinel.alert.dto.AlertResponse;
import com.sentinel.alert.dto.CreateAlertRequest;
import com.sentinel.alert.mapper.AlertMapper;
import com.sentinel.alert.notification.service.NotificationChannelDispatcher;
import com.sentinel.alert.producer.AlertEventProducer;
import com.sentinel.alert.repository.AlertAssignmentRepository;
import com.sentinel.alert.repository.AlertHistoryRepository;
import com.sentinel.alert.repository.AlertRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertServiceTest {

    @Mock private AlertRepository alertRepository;
    @Mock private AlertHistoryRepository historyRepository;
    @Mock private AlertAssignmentRepository assignmentRepository;
    @Mock private NotificationChannelDispatcher dispatcher;
    @Mock private AlertEventProducer producer;

    private final AlertMapper mapper = new AlertMapper();
    private AlertService alertService;

    @BeforeEach
    void setUp() {
        alertService = new AlertService(
                alertRepository, historyRepository, assignmentRepository, dispatcher, producer, mapper
        );
    }

    @Test
    void createAlert_SuccessfulCreation() {
        CreateAlertRequest req = new CreateAlertRequest(
                "SQL Injection Detected", "Exploit payload in query string", "SQL_INJECTION",
                AlertSeverity.CRITICAL, 85.0, "threat-service", "/api/v1/auth", "user1", "192.168.1.10", "corr-1", null
        );

        when(alertRepository.save(any(AlertEntity.class))).thenAnswer(i -> i.getArgument(0));

        AlertResponse response = alertService.createAlert(req);

        assertNotNull(response);
        assertEquals("SQL Injection Detected", response.title());
        assertEquals(AlertSeverity.CRITICAL, response.severity());
        assertEquals(AlertStatus.NEW, response.status());
        verify(producer, times(1)).publishAlertCreated(any());
    }
}
