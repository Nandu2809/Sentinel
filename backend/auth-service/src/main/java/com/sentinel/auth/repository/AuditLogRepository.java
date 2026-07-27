package com.sentinel.auth.repository;

import com.sentinel.auth.domain.model.AuditLogEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLogEntity, UUID> {
}

