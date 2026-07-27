package com.sentinel.monitoring.repository;

import com.sentinel.monitoring.domain.model.ApiEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApiRepository extends JpaRepository<ApiEntity, UUID> {
    boolean existsByNameIgnoreCase(String name);
    Optional<ApiEntity> findByPathPatternAndMethod(String pathPattern, String method);
}
