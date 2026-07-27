package com.sentinel.threat.repository;

import com.sentinel.threat.domain.entity.SecurityEventEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SecurityEventRepository extends JpaRepository<SecurityEventEntity, UUID> {
}
