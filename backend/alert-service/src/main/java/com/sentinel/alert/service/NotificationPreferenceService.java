package com.sentinel.alert.service;

import com.sentinel.alert.domain.entity.NotificationPreferenceEntity;
import com.sentinel.alert.domain.model.AlertSeverity;
import com.sentinel.alert.dto.NotificationPreferenceResponse;
import com.sentinel.alert.dto.UpdatePreferenceRequest;
import com.sentinel.alert.mapper.AlertMapper;
import com.sentinel.alert.repository.NotificationPreferenceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationPreferenceService {
    private final NotificationPreferenceRepository preferenceRepository;
    private final AlertMapper mapper;

    public NotificationPreferenceService(NotificationPreferenceRepository preferenceRepository, AlertMapper mapper) {
        this.preferenceRepository = preferenceRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public NotificationPreferenceResponse getPreferenceForUser(String userId) {
        return preferenceRepository.findByUserId(userId)
                .map(mapper::toPreferenceResponse)
                .orElseGet(() -> new NotificationPreferenceResponse(null, userId, true, true, null, AlertSeverity.MEDIUM, null));
    }

    @Transactional
    public NotificationPreferenceResponse updatePreference(UpdatePreferenceRequest request) {
        NotificationPreferenceEntity preference = preferenceRepository.findByUserId(request.userId())
                .orElseGet(() -> new NotificationPreferenceEntity(
                        request.userId(),
                        Boolean.TRUE.equals(request.emailEnabled()),
                        Boolean.TRUE.equals(request.websocketEnabled()),
                        request.webhookUrl(),
                        request.minSeverity()
                ));

        preference.updatePreferences(
                request.emailEnabled() != null ? request.emailEnabled() : preference.isEmailEnabled(),
                request.websocketEnabled() != null ? request.websocketEnabled() : preference.isWebsocketEnabled(),
                request.webhookUrl() != null ? request.webhookUrl() : preference.getWebhookUrl(),
                request.minSeverity() != null ? request.minSeverity() : preference.getMinSeverity()
        );

        NotificationPreferenceEntity saved = preferenceRepository.save(preference);
        return mapper.toPreferenceResponse(saved);
    }
}
