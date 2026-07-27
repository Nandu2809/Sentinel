package com.sentinel.alert.escalation;

import com.sentinel.alert.domain.entity.AlertEntity;
import com.sentinel.alert.domain.entity.EscalationRuleEntity;
import com.sentinel.alert.domain.model.AlertSeverity;
import com.sentinel.alert.notification.service.NotificationChannelDispatcher;
import com.sentinel.alert.repository.AlertRepository;
import com.sentinel.alert.repository.EscalationRuleRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EscalationEngineTest {

    @Mock private AlertRepository alertRepository;
    @Mock private EscalationRuleRepository escalationRuleRepository;
    @Mock private NotificationChannelDispatcher dispatcher;

    private EscalationEngineService escalationEngineService;

    @BeforeEach
    void setUp() {
        escalationEngineService = new EscalationEngineService(alertRepository, escalationRuleRepository, dispatcher);
    }

    @Test
    void evaluateEscalations_EscalatesBreachedAlerts() {
        EscalationRuleEntity rule = new EscalationRuleEntity(
                "Critical Alert Timeout", AlertSeverity.CRITICAL, 15, "SOC_LEAD", true
        );
        AlertEntity alert = new AlertEntity(
                "ALT-12345678", "SQLi Detected", "Desc", "SQL_INJECTION",
                AlertSeverity.CRITICAL, 90.0, "threat-service", "/api/v1/auth", null, null, null, null
        );

        when(escalationRuleRepository.findByEnabledTrue()).thenReturn(List.of(rule));
        when(alertRepository.findUnacknowledgedAlertsPastSla(eq(AlertSeverity.CRITICAL), any())).thenReturn(List.of(alert));

        escalationEngineService.evaluateEscalations();

        verify(alertRepository, times(1)).save(alert);
        verify(dispatcher, times(2)).dispatch(eq(alert), any(), any());
    }
}
