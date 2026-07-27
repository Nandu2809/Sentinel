# Sentinel – Intelligent API Security & Threat Monitoring Platform

![Build Status](https://img.shields.io/badge/build-passing-brightgreen)
![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-green)
![Python](https://img.shields.io/badge/Python-3.12-blue)
![Kubernetes](https://img.shields.io/badge/Kubernetes-Cloud--Native-blue)
![License](https://img.shields.io/badge/license-MIT-blue)

**Sentinel** is an enterprise-grade, hybrid security intelligence platform designed to protect microservice architectures from API abuse, signature-based OWASP threats, zero-day vulnerabilities, and behavioral anomalies. 

Combining **Java 21 Spring Boot 3.x** microservices with a **Python 3.12 FastAPI Machine Learning Anomaly Engine**, Sentinel delivers real-time API monitoring, automated risk scoring, multi-channel SOC alerting, and explainable AI insights.

---

## 🏗️ Platform Architecture

```
 +---------------------------------------------------------------------------------------------------------+
 |                                          ENTERPRISE TRAFFIC                                             |
 |                               (HTTPS / TLS 1.3 via NGINX Ingress Controller)                            |
 +---------------------------------------------------------------------------------------------------------+
                                                       |
                                                       v
 +---------------------------------------------------------------------------------------------------------+
 |                                  KUBERNETES NAMESPACE: sentinel-system                                  |
 |                                                                                                         |
 |  [1. API Gateway Service] ──> gateway-service:8080 (Redis Rate Limiting & JWT Validation)              |
 |                                                                                                         |
 |  [2. Security Microservice Reactor]                                                                     |
 |   ├── auth-service:8081        (Identity Provider, JWT, BCrypt, RBAC)                               |
 |   ├── monitoring-service:8082  (API Traffic Collection, Metrics Aggregation)                            |
 |   ├── threat-service:8083      (Rule Engine, OWASP Signatures, Threat Events)                          |
 |   ├── risk-service:8084        (Multi-Factor Risk Scoring, Security Score 0-100)                      |
 |   ├── alert-service:8085       (SOC Alert Lifecycle, WebSocket/Email, SLA Escalation)                  |
 |   └── report-service:8086      (Dashboard Summary APIs, PDF/CSV/JSON Exporters)                       |
 |                                                                                                         |
 |  [3. AI Anomaly Engine (ai-engine/)]                                                                    |
 |   └── ai-engine:8000           (FastAPI, Isolation Forest, One-Class SVM, DBSCAN, XAI Explainer)       |
 |                                                                                                         |
 |  [4. Stateful Infrastructure]                                                                           |
 |   ├── PostgreSQL 16            (Relational Storage & Flyway Migrations)                                 |
 |   ├── Redis 7                  (Distributed Cache & Token Bucket Rate Limiting)                         |
 |   └── Apache Kafka & Zookeeper (Real-Time Asynchronous Event Bus)                                      |
 +---------------------------------------------------------------------------------------------------------+
```

---

## ✨ Key Features

- **Hybrid Threat Intelligence**: Signature-based OWASP rule evaluation fused with AI behavioral anomaly detection.
- **Multi-Factor Risk Scoring Engine**: Calculates real-time entity risk profiles for Users, Endpoints, and IPs.
- **SOC Alert Lifecycle & SLA Escalation**: Automated lifecycle state machine (`NEW` -> `ACKNOWLEDGED` -> `RESOLVED` -> `CLOSED`) with background cron SLA escalation.
- **Explainable AI (XAI)**: Generates human-readable primary detection reasons and feature attribution metrics.
- **Executive Security Dashboards**: High-throughput REST APIs supplying real-time metrics for Angular dashboards.
- **Automated Report Exporter**: PDF, CSV, and JSON security posture reports.
- **Post-Quantum Cryptography (PQC) Ready**: Architectural migration path for NIST FIPS 203 (ML-KEM) and FIPS 204 (ML-DSA).

---

## 🛠️ Technology Stack

- **Backend**: Java 21, Spring Boot 3.3.5, Spring Cloud Gateway, Spring Security, Spring Data JPA, Flyway, Micrometer, Actuator, OpenAPI.
- **AI Engine**: Python 3.12, FastAPI, Scikit-learn, Pandas, NumPy, Joblib, Pytest.
- **Databases & Event Bus**: PostgreSQL 16, Redis 7, Apache Kafka.
- **Containerization & Orchestration**: Docker, Kubernetes, Helm, NGINX Ingress, HPA.
- **DevSecOps & CI/CD**: GitHub Actions, Trivy, Gitleaks, OWASP Dependency Check, Prometheus, Grafana, ELK Stack.

---

## 🚀 Quick Start & Deployment

### Local Docker Spin-Up
```bash
# 1. Clone repository
git clone https://github.com/madugundunanda-ui/Sentinel.git
cd Sentinel

# 2. Configure Environment Template
cp .env.example .env

# 3. Launch Platform via Docker Compose
docker compose -f infrastructure/docker/docker-compose.yml up --build -d
```

### Kubernetes Production Deployment
```bash
kubectl apply -f infrastructure/kubernetes/namespace.yaml
kubectl apply -f infrastructure/kubernetes/configmaps/
kubectl apply -f infrastructure/kubernetes/secrets/
kubectl apply -f infrastructure/kubernetes/persistent-volumes/
kubectl apply -f infrastructure/kubernetes/deployments/
kubectl apply -f infrastructure/kubernetes/services/
kubectl apply -f infrastructure/kubernetes/ingress/
kubectl apply -f infrastructure/kubernetes/autoscaling/
```

---

## 📖 Documentation Directory

- 📐 [Architecture Guide](docs/deployment/architecture.md)
- 🐳 [Docker Deployment Guide](docs/deployment/docker-guide.md)
- ☸️ [Kubernetes Operations Guide](docs/deployment/kubernetes-guide.md)
- 🔄 [CI/CD Workflows](docs/deployment/ci-cd-guide.md)
- 📊 [Monitoring & Prometheus Guide](docs/deployment/monitoring-guide.md)
- 🛡️ [DevSecOps Security Controls](docs/deployment/security-guide.md)
- 🔒 [Security Audit Report](docs/security/security-audit-report.md)
- 🎯 [Penetration Testing Framework](docs/security/penetration-testing-report.md)
- ⚛️ [Post-Quantum Readiness Architecture](docs/security/post-quantum-readiness.md)
- ⚡ [Performance & Load Test Benchmarks](docs/performance/performance-report.md)
- 📄 [Production Readiness Report](docs/production-readiness-report.md)

---

## 📜 License
Distributed under the MIT License. See [`LICENSE`](LICENSE) for details.
