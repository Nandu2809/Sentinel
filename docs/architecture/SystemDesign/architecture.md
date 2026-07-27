# Sentinel Architecture

## Sprint 1 Scope

Sprint 1 implements the authentication bounded context as `auth-service`, supported by `common-library`.

Implemented capabilities:

- JWT access tokens with short TTL.
- Opaque refresh tokens stored only as SHA-256 hashes.
- Refresh-token rotation and logout revocation.
- BCrypt password hashing.
- Role-based access control using permissions as authorities.
- CRUD APIs for users, roles, and permissions.
- Audit log persistence for security-relevant events.
- OpenAPI documentation and Actuator health/metrics endpoints.
- PostgreSQL schema managed by Flyway.

## Bounded Contexts

- `auth-service`: authentication, session lifecycle, RBAC, and audit events for identity actions.
- `gateway-service`: future API gateway, rate limiting, request normalization, and perimeter policies.
- `monitoring-service`: future API telemetry ingestion.
- `threat-service`: future detection rules, anomaly scoring, and AI integration boundary.
- `alert-service`: future alert lifecycle and escalation policy.
- `notification-service`: future delivery integrations.
- `report-service`: future compliance and analytics reports.

## Security Design

The service is stateless for access-token authentication. CSRF protection is disabled because browser sessions and cookies are not used; CORS is explicit and environment-driven. Security headers are enabled by default. Passwords, access tokens, and refresh tokens are never logged.

Refresh tokens are opaque random values. Only hashes are persisted, so a database read does not expose usable session credentials. Token rotation revokes the old token whenever a new access token is issued.

## PostgreSQL Schema

Core tables:

- `users`
- `roles`
- `permissions`
- `role_permissions`
- `refresh_tokens`
- `audit_logs`

The schema is normalized and avoids unnecessary tables for Sprint 1.

## Future Readiness

The service boundary is deployable as a container and can be lifted into Kubernetes with ConfigMaps, Secrets, NetworkPolicies, and horizontal scaling. AI threat detection and post-quantum cryptography readiness belong behind explicit interfaces in later bounded contexts rather than inside controllers.

