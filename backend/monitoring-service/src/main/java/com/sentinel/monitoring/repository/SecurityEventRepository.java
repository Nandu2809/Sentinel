package com.sentinel.monitoring.repository;

import com.sentinel.monitoring.domain.model.SecurityEventEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SecurityEventRepository extends JpaRepository<SecurityEventEntity, UUID> {
}
