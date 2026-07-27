# Sentinel Disaster Recovery & Chaos Resilience Specification

## 1. Resilience Scenarios & Verification

### Scenario A: PostgreSQL Database Temporary Outage
- **Simulation**: Primary PostgreSQL container killed during active API traffic.
- **System Response**:
  - `HikariCP` connection pool logs retry warnings and queues requests.
  - Redis cache continues serving read-only dashboard overview metrics.
  - Gateway returns standard HTTP 503 Service Unavailable without stack trace exposure.
- **Recovery**: Automatic reconnection upon PostgreSQL container restart; Flyway validates schema integrity.

### Scenario B: Apache Kafka Broker Disconnect
- **Simulation**: Kafka broker paused for 60 seconds.
- **System Response**:
  - Microservices queue events locally in Spring `@KafkaListener` and Spring Retry templates.
  - Threat detection continues processing via direct REST fallback.
- **Recovery**: Consumer groups automatically rebalance and drain buffered offset lag upon Kafka reconnect.

### Scenario C: Redis Cache Node Failure
- **Simulation**: Redis process terminated.
- **System Response**:
  - `DashboardOverviewService` falls back gracefully to direct PostgreSQL query execution.
- **Recovery**: Redis container recovers and repopulates key caches automatically.
