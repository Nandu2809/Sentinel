# Sentinel Deployment Architecture

## System Topography
Sentinel is architected as an enterprise, cloud-native cybersecurity platform.

```
 [Enterprise Ingress] ──> NGINX Ingress Controller (TLS 1.3)
                                  │
                                  v
                       [gateway-service (8080)]
                                  │
           ┌──────────────────────┼──────────────────────┐
           v                      v                      v
   [auth-service:8081]  [monitoring-service:8082]  [threat-service:8083]
           │                      │                      │
           v                      v                      v
   [risk-service:8084]   [alert-service:8085]    [report-service:8086]
                                  │
                                  v
                        [ai-engine:8000 (Python)]
```

## Infrastructure Core
- **Database Layer**: PostgreSQL 16 (Relational schemas) & MongoDB (Telemetry & Feature Store).
- **Cache Layer**: Redis 7.
- **Messaging Event Bus**: Apache Kafka & Zookeeper.
- **Monitoring**: Prometheus (Metrics Scrape) & Grafana (Dashboards).
- **Logging**: ELK Stack (Elasticsearch, Logstash, Kibana).
