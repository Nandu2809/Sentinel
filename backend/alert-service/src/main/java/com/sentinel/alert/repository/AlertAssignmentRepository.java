package com.sentinel.alert.repository;

import com.sentinel.alert.domain.entity.AlertAssignmentEntity;
import com.sentinel.alert.domain.entity.AlertEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertAssignmentRepository extends JpaRepository<AlertAssignmentEntity, UUID> {
    List<AlertAssignmentEntity> findByAlertOrderByTimestampDesc(AlertEntity alert);
}
