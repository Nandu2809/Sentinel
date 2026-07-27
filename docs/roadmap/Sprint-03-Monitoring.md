# Sprint 03: Telemetry Ingestion & Real-Time Monitoring

## Objectives
Implement the `monitoring-service` to ingest, sanitize, and buffer real-time API traffic metrics, HTTP request metadata, latency metrics, and error rates for threat analysis.

## Features
- **High-Throughput Ingestion**: Asynchronous Kafka/RabbitMQ event ingestion pipeline for raw API request logs.
- **Payload Anonymization**: Strip sensitive PII, passwords, and authorization tokens prior to storage.
- **Prometheus Metrics Exporter**: Expose custom Micrometer counters for HTTP throughput, 4xx/5xx status frequencies, and P99 response latencies.
- **Time-Series Storage**: Persist aggregated metrics into PostgreSQL/TimescaleDB.

## Acceptance Criteria
1. Gateway streams API request logs asynchronously with sub-5ms overhead.
2. Anonymizer guarantees zero raw tokens or passwords in stored logs.
3. Prometheus endpoint `/actuator/prometheus` exports real-time API latency histograms.

## Dependencies
- Sprint 02 (`gateway-service`), Apache Kafka / RabbitMQ, Micrometer, Prometheus.

## Deliverables
- `monitoring-service` microservice module.
- Telemetry ingestion schema and Kafka event producers/consumers.
- Grafana dashboard JSON models (`infrastructure/monitoring/`).
