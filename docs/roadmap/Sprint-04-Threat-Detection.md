# Sprint 04: Threat Detection Rules Engine

## Objectives
Develop `threat-service` to evaluate live API telemetry against deterministic threat rules (credential stuffing, SQL injection attempts, brute-force patterns, rate spikes, and GEO-IP anomalies).

## Features
- **Deterministic Rules Evaluator**: Configurable rule engine matching HTTP signatures, payload patterns, and request rates.
- **Threat Scoring Model**: Assign real-time threat scores (0-100) per IP address, user account, and API endpoint.
- **Blacklist Manager**: Dynamic IP and token revocation list pushed to Redis for edge enforcement.
- **Rule Definition API**: CRUD APIs for security administrators to add and tune threat detection rules.

## Acceptance Criteria
1. Brute-force login attempts (5+ failures in 60s) trigger an immediate high-risk threat score (>80).
2. SQL injection pattern detection in query parameters flags security alerts within 100ms.
3. High-confidence threats automatically push blacklisted IP records to Redis.

## Dependencies
- Sprint 01 (`auth-service`), Sprint 03 (`monitoring-service`), Redis 7.

## Deliverables
- `threat-service` Spring Boot microservice.
- Threat rule engine specification and default rule definitions.
- Unit and integration tests for rule evaluations.
