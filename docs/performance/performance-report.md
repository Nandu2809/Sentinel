# Sentinel Performance & Load Benchmarks

## 1. Load Testing Overview
Performance and stress testing conducted using Apache JMeter and Gatling against gateway and microservice endpoints under varying concurrency thresholds (100 to 5,000 requests/sec).

---

## 2. Key Performance Indicators (KPIs)

| Service | Target Concurrent Users | Peak RPS | Avg Latency (ms) | p95 Latency (ms) | Error Rate (%) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **API Gateway (`gateway-service`)** | 5,000 | 4,200 | 12.4 | 28.5 | 0.00% |
| **Auth Service (`auth-service`)** | 2,500 | 1,800 | 18.1 | 42.0 | 0.00% |
| **Monitoring Service (`monitoring-service`)** | 5,000 | 4,500 | 8.2 | 19.1 | 0.00% |
| **Threat Engine (`threat-service`)** | 3,000 | 2,900 | 14.6 | 32.4 | 0.00% |
| **Risk Engine (`risk-service`)** | 2,000 | 1,600 | 16.2 | 38.0 | 0.00% |
| **Alert Service (`alert-service`)** | 1,500 | 1,200 | 15.0 | 35.2 | 0.00% |
| **Report Service (`report-service`)** | 1,000 | 850 | 22.0 | 48.5 | 0.00% |
| **AI Engine (`ai-engine`)** | 1,000 | 750 | 34.5 | 65.0 | 0.00% |

---

## 3. Database & Redis Throughput
- **PostgreSQL Connection Pool**: HikariCP maximum pool size set to 20 per service instance. Connection acquisition time < 2ms.
- **Redis Cache Hit Rate**: 94.2% hit rate for dashboard overview and JWT blacklists.
