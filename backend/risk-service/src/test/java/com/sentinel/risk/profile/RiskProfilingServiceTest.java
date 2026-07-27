package com.sentinel.risk.profile;

import com.sentinel.risk.domain.entity.RiskProfileEntity;
import com.sentinel.risk.domain.model.EntityType;
import com.sentinel.risk.dto.CalculateRiskRequest;
import com.sentinel.risk.events.RiskEventPublisher;
import com.sentinel.risk.repository.EndpointRiskRepository;
import com.sentinel.risk.repository.IpRiskRepository;
import com.sentinel.risk.repository.RiskHistoryRepository;
import com.sentinel.risk.repository.RiskProfileRepository;
import com.sentinel.risk.repository.UserRiskRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RiskProfilingServiceTest {

    @Mock private RiskProfileRepository profileRepository;
    @Mock private RiskHistoryRepository historyRepository;
    @Mock private UserRiskRepository userRiskRepository;
    @Mock private IpRiskRepository ipRiskRepository;
    @Mock private EndpointRiskRepository endpointRiskRepository;
    @Mock private RiskEventPublisher eventPublisher;

    private RiskProfilingService profilingService;

    @BeforeEach
    void setUp() {
        profilingService = new RiskProfilingService(
                profileRepository, historyRepository, userRiskRepository, ipRiskRepository, endpointRiskRepository, eventPublisher
        );
    }

    @Test
    void updateEntityProfile_NewProfileCreated() {
        CalculateRiskRequest req = new CalculateRiskRequest(
                EntityType.USER, "user-456", "XSS", "HIGH", false, false, false
        );

        when(profileRepository.findByEntityTypeAndEntityId(EntityType.USER, "user-456")).thenReturn(Optional.empty());
        when(profileRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        RiskProfileEntity profile = profilingService.updateEntityProfile(req, 75.0);

        assertNotNull(profile);
        assertEquals(EntityType.USER, profile.getEntityType());
        assertEquals("user-456", profile.getEntityId());
        assertEquals(75.0, profile.getCurrentRiskScore());
    }
}
