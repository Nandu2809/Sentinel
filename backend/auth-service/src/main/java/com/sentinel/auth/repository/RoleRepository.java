package com.sentinel.auth.repository;

import com.sentinel.auth.domain.model.RoleEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<RoleEntity, UUID> {
    Optional<RoleEntity> findByName(String name);

    boolean existsByName(String name);
}

