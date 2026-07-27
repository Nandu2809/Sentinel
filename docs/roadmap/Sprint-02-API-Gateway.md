# Sprint 02: API Gateway & Edge Perimeter Security

## Objectives
Build a centralized perimeter gateway (`gateway-service` / `api-gateway`) implementing request routing, rate-limiting, JWT validation, and IP filtering before traffic reaches internal microservices.

## Features
- **Dynamic Routing**: Route incoming traffic to downstream bounded contexts based on path prefixes (`/api/v1/auth`, `/api/v1/users`, `/api/v1/telemetry`).
- **Token Relay & Validation**: Intercept requests, validate JWT signatures against `auth-service`, and pass authenticated principal headers downstream.
- **Distributed Rate Limiting**: Redis-backed leaky-bucket rate limiter per IP address and API key.
- **CORS & Security Headers**: Global CORS management and OWASP security header injection.

## Acceptance Criteria
1. Unauthenticated requests to protected endpoints return HTTP 401 at the gateway edge without touching internal microservices.
2. Requests exceeding 100 req/min return HTTP 429 Too Many Requests.
3. Gateway correctly relays user ID and roles via internal request headers.
4. Gateway health check endpoint reports Redis and downstream service readiness.

## Dependencies
- Sprint 01 (`auth-service`, `common-library`), Spring Cloud Gateway, Redis 7.

## Deliverables
- `gateway-service` microservice module.
- Redis rate limiting configuration rules.
- Perimeter security integration tests.
