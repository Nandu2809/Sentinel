package com.sentinel.auth.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 320)
    private String email;

    @Column(nullable = false, unique = true, length = 80)
    private String username;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false, length = 80)
    private String firstName;

    @Column(nullable = false, length = 80)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private UserStatus status;

    @Column(nullable = false)
    private boolean emailVerified;

    @Column(nullable = false)
    private int failedLoginAttempts;

    private Instant lastLoginAt;

    private Instant passwordChangedAt;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private RoleEntity role;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @Version
    private long version;

    protected UserEntity() {
    }

    public UserEntity(String email, String username, String passwordHash, String firstName, String lastName, RoleEntity role) {
        this.id = UUID.randomUUID();
        this.email = email.toLowerCase();
        this.username = username;
        this.passwordHash = passwordHash;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.status = UserStatus.PENDING_VERIFICATION;
        this.emailVerified = false;
        this.failedLoginAttempts = 0;
        this.passwordChangedAt = Instant.now();
    }

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
        if (id == null) {
            id = UUID.randomUUID();
        }
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }

    public void activate() {
        this.status = UserStatus.ACTIVE;
        this.emailVerified = true;
    }

    public void markLoginSucceeded() {
        this.failedLoginAttempts = 0;
        this.lastLoginAt = Instant.now();
    }

    public void markLoginFailed(int lockThreshold) {
        this.failedLoginAttempts += 1;
        if (failedLoginAttempts >= lockThreshold) {
            this.status = UserStatus.LOCKED;
        }
    }

    public void changePassword(String passwordHash) {
        this.passwordHash = passwordHash;
        this.passwordChangedAt = Instant.now();
    }

    public void updateProfile(String firstName, String lastName, UserStatus status, RoleEntity role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.status = status;
        this.role = role;
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public UserStatus getStatus() {
        return status;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public int getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public Instant getLastLoginAt() {
        return lastLoginAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public RoleEntity getRole() {
        return role;
    }
}

