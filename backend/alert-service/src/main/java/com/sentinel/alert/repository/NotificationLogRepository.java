package com.sentinel.alert.repository;

import com.sentinel.alert.domain.entity.NotificationLogEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationLogRepository extends JpaRepository<NotificationLogEntity, UUID> {
}
