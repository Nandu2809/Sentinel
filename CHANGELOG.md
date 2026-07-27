# Changelog

All notable changes to the **Sentinel Platform** will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2026-07-27

### Added
- **Sprint 1**: Initial release of `auth-service` with JWT authentication, BCrypt hashing, and Spring Security.
- **Sprint 2**: `gateway-service` with Spring Cloud Gateway, Redis Rate Limiting, and CORS controls.
- **Sprint 3**: `monitoring-service` with API inventory registration and request metric logging.
- **Sprint 4**: `threat-service` with OWASP rule evaluation engine, threat severity calculator, and incident management.
- **Sprint 5**: `risk-service` with multi-factor risk scoring engine and entity risk profiling.
- **Sprint 6**: `alert-service` with SOC alert lifecycle management, email/WebSocket notifications, and SLA escalation engine.
- **Sprint 7**: `report-service` with security overview APIs, PDF/CSV/JSON report exporters, and `@Scheduled` report generation.
- **Sprint 8**: `ai-engine` with Python 3.12 FastAPI microservice, Isolation Forest, One-Class SVM, DBSCAN clustering, and Explainable AI (XAI).
- **Sprint 9**: Production Kubernetes manifests, multi-stage non-root Dockerfiles, GitHub Actions CI/CD workflows, Prometheus scrape config, and deployment documentation.
- **Sprint 10**: OWASP Top 10 security audit, penetration testing scenarios, load testing reports, post-quantum readiness roadmap, and v1.0.0 release engineering.
