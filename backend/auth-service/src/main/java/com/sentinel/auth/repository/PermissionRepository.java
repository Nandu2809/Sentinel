package com.sentinel.auth.repository;

import com.sentinel.auth.domain.model.PermissionEntity;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<PermissionEntity, UUID> {
    Optional<PermissionEntity> findByName(String name);

    boolean existsByName(String name);

    List<PermissionEntity> findByIdIn(Collection<UUID> ids);
}

