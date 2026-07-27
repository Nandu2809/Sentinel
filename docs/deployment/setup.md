# Sentinel Setup Guide

## Requirements

- Java 21
- Maven 3.9+
- Docker and Docker Compose

## Environment Variables

Required for production-like runs:

- `POSTGRES_DB`
- `POSTGRES_USER`
- `POSTGRES_PASSWORD`
- `JWT_SECRET` - at least 32 bytes
- `REDIS_PASSWORD`

Optional:

- `JWT_ISSUER`
- `JWT_ACCESS_TOKEN_TTL`
- `JWT_REFRESH_TOKEN_TTL`
- `SENTINEL_ALLOWED_ORIGINS`
- `SENTINEL_BOOTSTRAP_ADMIN_EMAIL`
- `SENTINEL_BOOTSTRAP_ADMIN_USERNAME`
- `SENTINEL_BOOTSTRAP_ADMIN_PASSWORD`

The bootstrap admin password must satisfy the password policy: at least 12 characters with uppercase, lowercase, digit, and special character.

## Build

```powershell
mvn clean verify
```

## Run With Docker Compose

```powershell
docker compose -f docker/docker-compose.yml --env-file .env up --build
```

## Run Locally

Start PostgreSQL, then run:

```powershell
mvn -pl auth-service -am spring-boot:run
```

Swagger UI:

```text
http://localhost:8081/swagger-ui/index.html
```

Readiness:

```text
http://localhost:8081/actuator/health/readiness
```

