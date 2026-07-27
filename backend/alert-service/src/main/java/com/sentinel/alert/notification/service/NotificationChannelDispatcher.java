package com.sentinel.alert.notification.service;

import com.sentinel.alert.domain.entity.AlertEntity;
import com.sentinel.alert.domain.entity.NotificationLogEntity;
import com.sentinel.alert.domain.model.NotificationChannel;
import com.sentinel.alert.notification.NotificationPayload;
import com.sentinel.alert.notification.provider.NotificationProvider;
import com.sentinel.alert.repository.NotificationLogRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationChannelDispatcher {
    private final List<NotificationProvider> providers;
    private final NotificationLogRepository notificationLogRepository;

    public NotificationChannelDispatcher(List<NotificationProvider> providers,
                                         NotificationLogRepository notificationLogRepository) {
        this.providers = providers;
        this.notificationLogRepository = notificationLogRepository;
    }

    @Transactional
    public void dispatch(AlertEntity alert, NotificationChannel channel, String recipient) {
        NotificationPayload payload = new NotificationPayload(
                alert.getId(), alert.getAlertCode(), alert.getTitle(),
                alert.getDescription(), alert.getSeverity(), alert.getRiskScore(),
                alert.getAffectedApi(), recipient
        );

        providers.stream()
                .filter(p -> p.supports(channel))
                .forEach(p -> {
                    try {
                        p.sendNotification(payload);
                        notificationLogRepository.save(new NotificationLogEntity(
                                alert, channel, recipient != null ? recipient : "DEFAULT", "SUCCESS", null
                        ));
                    } catch (Exception e) {
                        notificationLogRepository.save(new NotificationLogEntity(
                                alert, channel, recipient != null ? recipient : "DEFAULT", "FAILED", e.getMessage()
                        ));
                    }
                });
    }
}
