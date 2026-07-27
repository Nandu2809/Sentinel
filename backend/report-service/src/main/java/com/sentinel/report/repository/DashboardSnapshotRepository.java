package com.sentinel.report.repository;

import com.sentinel.report.domain.entity.DashboardSnapshotEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DashboardSnapshotRepository extends JpaRepository<DashboardSnapshotEntity, UUID> {
    Optional<DashboardSnapshotEntity> findTopByOrderByCapturedAtDesc();
}
