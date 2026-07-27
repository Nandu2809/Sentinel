package com.sentinel.auth.config;

import com.sentinel.auth.domain.model.PermissionEntity;
import com.sentinel.auth.domain.model.RoleEntity;
import com.sentinel.auth.domain.model.UserEntity;
import com.sentinel.auth.repository.PermissionRepository;
import com.sentinel.auth.repository.RoleRepository;
import com.sentinel.auth.repository.UserRepository;
import com.sentinel.auth.security.PasswordPolicyValidator;
import com.sentinel.auth.security.PermissionCatalog;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
class DataInitializer {
    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    CommandLineRunner initializeAuthData(PermissionRepository permissionRepository, RoleRepository roleRepository,
                                         UserRepository userRepository, PasswordEncoder passwordEncoder,
                                         PasswordPolicyValidator passwordPolicyValidator,
                                         BootstrapProperties bootstrapProperties) {
        return args -> seed(permissionRepository, roleRepository, userRepository, passwordEncoder,
                passwordPolicyValidator, bootstrapProperties);
    }

    void seed(PermissionRepository permissionRepository, RoleRepository roleRepository, UserRepository userRepository,
              PasswordEncoder passwordEncoder, PasswordPolicyValidator passwordPolicyValidator,
              BootstrapProperties bootstrapProperties) {
        for (PermissionCatalog catalog : PermissionCatalog.all()) {
            permissionRepository.findByName(catalog.permissionName())
                    .orElseGet(() -> permissionRepository.save(new PermissionEntity(
                            catalog.permissionName(),
                            catalog.resource(),
                            catalog.action(),
                            "System permission for " + catalog.resource() + ":" + catalog.action())));
        }

        Set<PermissionEntity> allPermissions = PermissionCatalog.all().stream()
                .map(catalog -> permissionRepository.findByName(catalog.permissionName()).orElseThrow())
                .collect(Collectors.toSet());
        Set<PermissionEntity> userPermissions = allPermissions.stream()
                .filter(permission -> "AUTH_SELF".equals(permission.getName()))
                .collect(Collectors.toSet());

        RoleEntity securityAdmin = roleRepository.findByName("SECURITY_ADMIN")
                .orElseGet(() -> roleRepository.save(new RoleEntity("SECURITY_ADMIN", "Full security administrator", true)));
        securityAdmin.replacePermissions(allPermissions);
        roleRepository.save(securityAdmin);

        RoleEntity userRole = roleRepository.findByName("USER")
                .orElseGet(() -> roleRepository.save(new RoleEntity("USER", "Default authenticated user", true)));
        userRole.replacePermissions(userPermissions);
        roleRepository.save(userRole);

        createBootstrapAdminIfConfigured(userRepository, roleRepository, passwordEncoder, passwordPolicyValidator,
                bootstrapProperties);
    }

    private void createBootstrapAdminIfConfigured(UserRepository userRepository, RoleRepository roleRepository,
                                                  PasswordEncoder passwordEncoder,
                                                  PasswordPolicyValidator passwordPolicyValidator,
                                                  BootstrapProperties properties) {
        if (isBlank(properties.adminEmail()) || isBlank(properties.adminUsername()) || isBlank(properties.adminPassword())) {
            log.info("bootstrap_admin status=skipped reason=missing_environment");
            return;
        }
        if (userRepository.existsByEmailIgnoreCase(properties.adminEmail())) {
            log.info("bootstrap_admin status=skipped reason=already_exists email={}", properties.adminEmail());
            return;
        }

        passwordPolicyValidator.validate(properties.adminPassword());
        RoleEntity adminRole = roleRepository.findByName("SECURITY_ADMIN").orElseThrow();
        UserEntity admin = new UserEntity(
                properties.adminEmail(),
                properties.adminUsername(),
                passwordEncoder.encode(properties.adminPassword()),
                "Security",
                "Administrator",
                adminRole);
        admin.activate();
        userRepository.save(admin);
        log.info("bootstrap_admin status=created email={}", properties.adminEmail());
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
