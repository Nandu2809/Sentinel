package com.sentinel.alert.repository;

import com.sentinel.alert.domain.entity.AlertCommentEntity;
import com.sentinel.alert.domain.entity.AlertEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertCommentRepository extends JpaRepository<AlertCommentEntity, UUID> {
    List<AlertCommentEntity> findByAlertOrderByTimestampDesc(AlertEntity alert);
}
