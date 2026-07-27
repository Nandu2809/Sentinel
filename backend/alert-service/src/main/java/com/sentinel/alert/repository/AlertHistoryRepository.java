package com.sentinel.alert.repository;

import com.sentinel.alert.domain.entity.AlertEntity;
import com.sentinel.alert.domain.entity.AlertHistoryEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertHistoryRepository extends JpaRepository<AlertHistoryEntity, UUID> {
    List<AlertHistoryEntity> findByAlertOrderByTimestampDesc(AlertEntity alert);
}
