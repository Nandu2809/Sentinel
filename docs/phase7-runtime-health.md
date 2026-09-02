# Sentinel Phase 7 — Runtime Health Matrix

**Generated:** 2026-09-02  
**Infrastructure Stack:** Docker Compose (`infrastructure/docker/docker-compose.yml`)

## Microservices & Infrastructure Runtime Matrix

| Service Name | Port | Health Endpoint / Verification | Container Health | Status |
| :--- | :--- | :--- | :--- | :--- |
| **API Gateway** | 8088 (->8080) | `http://localhost:8088/actuator/health` | Healthy | **PASS (200)** |
| **Auth Service** | 8081 | `http://localhost:8081/actuator/health` | Healthy | **PASS (200)** |
| **Monitoring Service** | 8082 | `http://localhost:8082/actuator/health` | Healthy | **PASS (200)** |
| **Threat Service** | 8083 | `http://localhost:8083/actuator/health` | Healthy | **PASS (200)** |
| **Risk Service** | 8084 | `http://localhost:8084/actuator/health` | Healthy | **PASS (200)** |
| **Alert Service** | 8085 | `http://localhost:8085/actuator/health` | Healthy | **PASS (200)** |
| **Report Service** | 8086 | `http://localhost:8086/actuator/health` | Healthy | **PASS (200)** |
| **AI Engine** | 8000 | `http://localhost:8000/api/v1/ai/status` | Healthy | **PASS (200)** |
| **PostgreSQL** | 5432 | TCP socket ping `localhost:5432` | Healthy | **PASS** |
| **Redis** | 6379 | TCP socket ping `localhost:6379` | Healthy | **PASS** |
| **Kafka** | 9092 | TCP socket ping `localhost:9092` | Running | **PASS** |
| **Zookeeper** | 2181 | Internal link `zookeeper:2181` | Running | **PASS** |
| **Mailpit** | 8025 / 1025 | `http://localhost:8025/` | Healthy | **PASS (200)** |
| **Prometheus** | 9090 | `http://localhost:9090/` | Running | **PASS** |
| **Grafana** | 3000 | `http://localhost:3000/` | Running | **PASS** |

## Verification Methodology
- **Infrastructure Status:** `docker compose ps` shows 15/15 services running with healthy status indicators.
- **Direct Health Checks:** Verified via direct container healthchecks and local socket connection tests.
- **Public API Gateway:** Verified via `http://localhost:8088` forwarding to internal Spring Boot actuator endpoints.
