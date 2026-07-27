package com.sentinel.alert.repository;

import com.sentinel.alert.domain.entity.AlertEventEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertEventRepository extends JpaRepository<AlertEventEntity, UUID> {
}
