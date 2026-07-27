package com.sentinel.monitoring.repository;

import com.sentinel.monitoring.domain.model.ApiHealthEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApiHealthRepository extends JpaRepository<ApiHealthEntity, UUID> {
    Optional<ApiHealthEntity> findByServiceName(String serviceName);
}
