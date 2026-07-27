# Monitoring & Observability Operations Guide

## Prometheus Metrics Scrape
- Scrapes Spring Actuator `/actuator/prometheus` endpoints every 15 seconds across all 7 Java microservices.
- Scrapes Python FastAPI metrics from `ai-engine`.

## Grafana Dashboards
- Pre-configured Grafana instance accessible on port 3000.
- Standard metrics: Request Volume, HTTP Error Rates, Response Time Latency p95/p99, Threat Incident Rates, Security Score Trends, and AI Detection Velocity.
