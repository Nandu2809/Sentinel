package com.sentinel.auth.service;

import com.sentinel.auth.api.dto.CreateUserRequest;
import com.sentinel.auth.api.dto.UpdateUserRequest;
import com.sentinel.auth.api.dto.UserResponse;
import com.sentinel.auth.domain.model.AuditOutcome;
import com.sentinel.auth.domain.model.RoleEntity;
import com.sentinel.auth.domain.model.UserEntity;
import com.sentinel.auth.domain.model.UserStatus;
import com.sentinel.auth.mapper.AuthMapper;
import com.sentinel.auth.repository.RoleRepository;
import com.sentinel.auth.repository.UserRepository;
import com.sentinel.auth.security.PasswordPolicyValidator;
import com.sentinel.common.exception.BusinessException;
import com.sentinel.common.exception.ErrorCode;
import com.sentinel.common.security.SecurityEventType;
import java.util.List;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserAdministrationService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordPolicyValidator passwordPolicyValidator;
    private final AuthMapper mapper;
    private final AuditEventService auditEventService;

    public UserAdministrationService(UserRepository userRepository, RoleRepository roleRepository,
                                     PasswordEncoder passwordEncoder, PasswordPolicyValidator passwordPolicyValidator,
                                     AuthMapper mapper, AuditEventService auditEventService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.passwordPolicyValidator = passwordPolicyValidator;
        this.mapper = mapper;
        this.auditEventService = auditEventService;
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(mapper::toUserResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponse get(UUID id) {
        return mapper.toUserResponse(findUser(id));
    }

    @Transactional
    public UserResponse create(CreateUserRequest request, RequestMetadata metadata) {
        ensureUniqueUser(request.email(), request.username());
        passwordPolicyValidator.validate(request.password());
        RoleEntity role = findRole(request.roleId());
        UserEntity user = new UserEntity(
                request.email(),
                request.username(),
                passwordEncoder.encode(request.password()),
                request.firstName(),
                request.lastName(),
                role);
        if (request.status() == UserStatus.ACTIVE) {
            user.activate();
        }
        UserEntity saved = userRepository.save(user);
        auditEventService.record(null, SecurityEventType.USER_CREATED, AuditOutcome.SUCCESS, metadata,
                "User", saved.getId().toString(), "User created by administrator");
        return mapper.toUserResponse(saved);
    }

    @Transactional
    public UserResponse update(UUID id, UpdateUserRequest request, RequestMetadata metadata) {
        UserEntity user = findUser(id);
        RoleEntity role = findRole(request.roleId());
        user.updateProfile(request.firstName(), request.lastName(), request.status(), role);
        auditEventService.record(null, SecurityEventType.USER_UPDATED, AuditOutcome.SUCCESS, metadata,
                "User", user.getId().toString(), "User updated by administrator");
        return mapper.toUserResponse(user);
    }

    @Transactional
    public void delete(UUID id, RequestMetadata metadata) {
        UserEntity user = findUser(id);
        userRepository.delete(user);
        auditEventService.record(null, SecurityEventType.USER_DELETED, AuditOutcome.SUCCESS, metadata,
                "User", id.toString(), "User deleted by administrator");
    }

    private UserEntity findUser(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "User not found"));
    }

    private RoleEntity findRole(UUID id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Role not found"));
    }

    private void ensureUniqueUser(String email, String username) {
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new BusinessException(ErrorCode.DUPLICATE_RESOURCE, "Email is already registered");
        }
        if (userRepository.existsByUsernameIgnoreCase(username)) {
            throw new BusinessException(ErrorCode.DUPLICATE_RESOURCE, "Username is already registered");
        }
    }
}

