# Sprint 01: Authentication & Identity Management

## Objectives
Establish the core identity bounded context (`auth-service`) providing stateless JWT token issuance, secure refresh-token rotation, role-based access control (RBAC), user lifecycle management, and audit logging.

## Features
- **JWT Token Service**: Short-lived access token generation with HMAC-SHA256 signing and custom claims.
- **Refresh Token Rotation**: Opaque random refresh token issuance with SHA-256 hash storage in PostgreSQL.
- **RBAC Engine**: Dynamic permission verification for Users, Roles, and Permissions.
- **Database Migrations**: Flyway schema versioning (`V1__create_auth_schema.sql`).
- **Security Auditing**: Asynchronous audit event capture for security actions.

## Acceptance Criteria
1. `POST /api/v1/auth/login` returns a valid JWT access token and HTTP-only refresh token.
2. Expired access tokens return HTTP 401 Unauthorized with standard JSON error payload.
3. Refreshing tokens revokes the old refresh token and issues a new pair.
4. Passwords must pass the 12+ character policy containing uppercase, lowercase, digit, and special character.
5. All tests pass (`AuthServiceTest` and `PasswordPolicyValidatorTest`).

## Dependencies
- Spring Boot 3.3.5, Spring Security, Spring Data JPA, PostgreSQL 16, Redis 7, Flyway.

## Deliverables
- `auth-service` Spring Boot application.
- `common-library` error and response DTO contracts.
- Docker Compose configuration (`infrastructure/docker/docker-compose.yml`).
