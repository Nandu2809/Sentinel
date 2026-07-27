package com.sentinel.monitoring.service;

import com.sentinel.common.exception.BusinessException;
import com.sentinel.monitoring.api.dto.ApiResponseDto;
import com.sentinel.monitoring.api.dto.RegisterApiRequest;
import com.sentinel.monitoring.domain.model.ApiEntity;
import com.sentinel.monitoring.mapper.MonitoringMapper;
import com.sentinel.monitoring.repository.ApiRepository;
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
class ApiInventoryServiceTest {

    @Mock
    private ApiRepository apiRepository;

    @Mock
    private AuditEventService auditEventService;

    private MonitoringMapper mapper = new MonitoringMapper();
    private ApiInventoryService apiInventoryService;

    @BeforeEach
    void setUp() {
        apiInventoryService = new ApiInventoryService(apiRepository, mapper, auditEventService);
    }

    @Test
    void createApi_Success() {
        RegisterApiRequest req = new RegisterApiRequest("Auth API", "/api/v1/auth/*", "POST", true, 100, "Authentication endpoints");
        when(apiRepository.existsByNameIgnoreCase("Auth API")).thenReturn(false);
        when(apiRepository.save(any(ApiEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ApiResponseDto result = apiInventoryService.create(req, new RequestMetadata("127.0.0.1", "JUnit"));

        assertNotNull(result);
        assertEquals("Auth API", result.name());
        assertEquals("POST", result.method());
        assertTrue(result.enabled());
    }

    @Test
    void createApi_DuplicateName_ThrowsException() {
        RegisterApiRequest req = new RegisterApiRequest("Auth API", "/api/v1/auth/*", "POST", true, 100, "Authentication endpoints");
        when(apiRepository.existsByNameIgnoreCase("Auth API")).thenReturn(true);

        assertThrows(BusinessException.class, () -> apiInventoryService.create(req, new RequestMetadata("127.0.0.1", "JUnit")));
    }

    @Test
    void getApi_NotFound_ThrowsException() {
        UUID id = UUID.randomUUID();
        when(apiRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> apiInventoryService.get(id));
    }
}
