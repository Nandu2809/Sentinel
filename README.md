# Sentinel Backend

Sentinel is an enterprise-grade, AI-ready API security and threat monitoring platform. Sprint 1 implements the authentication bounded context with JWT authentication, refresh tokens, RBAC, audit logging, PostgreSQL persistence, OpenAPI documentation, and Docker Compose support.

## Modules

- `common-library` - shared API response, error, and security event contracts.
- `auth-service` - independently deployable Spring Boot authentication and RBAC service.

Future service folders are documented in `docs/architecture.md`; they should be implemented incrementally as independent deployables.

## Run Auth Service

1. Copy `.env.example` to `.env` and set strong secrets.
2. Start dependencies and service:

```powershell
docker compose -f docker/docker-compose.yml up --build
```

3. Open Swagger UI at `http://localhost:8081/swagger-ui/index.html`.

For local Maven execution:

```powershell
mvn -pl auth-service -am spring-boot:run
```

Required environment variables are listed in `docs/setup.md`.

