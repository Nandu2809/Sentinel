# Sentinel Repository Structure & Developer Guide

This document explains the organization of the **Sentinel** enterprise monorepo, detailing the purpose of every top-level directory and outlining the development workflow for contributors.

---

## 📂 Repository Layout

```text
Sentinel/
├── backend/                       # Spring Boot Microservices & Libraries
├── frontend/                      # User Interface & Dashboard Applications
├── ai-engine/                     # AI/ML Anomaly Detection & Threat Pipelines
├── infrastructure/                # Container Orchestration, IaC & Telemetry
├── docs/                          # Platform Specifications, ADRs & Roadmaps
├── .github/                       # CI/CD GitHub Actions Automation
├── scripts/                       # Operational & Startup Utility Scripts
├── .env.example                   # Environment Variables Reference
├── .gitignore                     # Git Exclusion Rules
├── LICENSE                        # Project License
├── pom.xml                        # Root Aggregator Maven POM
├── REPOSITORY_STRUCTURE.md        # Monorepo Architecture Guide (This File)
└── README.md                      # Platform Overview & Getting Started
```

---

## 🏛️ Top-Level Directory Purpose

### 1. `backend/`
Contains all server-side microservices, shared domain libraries, and Java-based security tools.
- **`pom.xml`**: Backend Parent POM managing dependencies, Java compiler flags (Java 21 target), and plugin versions across all services.
- **`common-library/`**: Shared Java library containing API response wrappers (`ApiResponse<T>`), global error contracts (`ApiErrorResponse`), business exceptions, and security event types.
- **`auth-service/`**: Production-ready Spring Boot microservice implementing JWT authentication, refresh-token rotation, BCrypt password validation, Flyway database migrations, and RBAC user management.
- **Bounded Context Placeholders**: `gateway-service/`, `user-service/`, `monitoring-service/`, `threat-service/`, `alert-service/`, `notification-service/`, `report-service/`, `config-server/`, `service-discovery/`, `api-gateway/`, `common-security/`, `shared-kernel/`.

### 2. `frontend/`
Houses user-facing client applications and operator dashboards.
- **`sentinel-dashboard/`**: React/Angular single-page application for platform operators, security analysts, and system administrators.

### 3. `ai-engine/`
Python-based Machine Learning runtime for real-time API anomaly detection and threat scoring.
- **`anomaly-detection/`**: Core inference scripts and detection model wrappers.
- **`training/`**: Model training and evaluation notebooks.
- **`models/`**: Serialized model artifacts (PyTorch/Scikit-Learn).
- **`datasets/`**: Training feature vectors and anonymized API request logs.
- **`pipelines/`**: Feature extraction and data preprocessing logic.
- **`experiments/`**: Hyperparameter tuning and experiment tracking logs.

### 4. `infrastructure/`
DevOps tooling, container definitions, and telemetry configurations.
- **`docker/`**: Multi-container Docker Compose files for local integration testing (`docker-compose.yml`).
- **`kubernetes/`**: K8s manifests, Helm charts, and service definitions.
- **`terraform/`**: Infrastructure as Code (IaC) for provisioning cloud resources.
- **`monitoring/`**: Prometheus metrics scraping rules and Grafana dashboards.
- **`logging/`**: Logstash, Elasticsearch, and Fluentd log aggregation configs.

### 5. `docs/`
Centralized repository documentation and sprint planning artifacts.
- **`architecture/`**: Architecture Decision Records (`ADR/`), C4 Models (`C4/`), Sequence Diagrams (`SequenceDiagrams/`), Threat Models (`ThreatModels/`), and System Architecture (`SystemDesign/`).
- **`api/`**: OpenAPI 3.0 specifications and API contract documentation.
- **`deployment/`**: Step-by-step setup and production deployment guides.
- **`database/`**: Entity Relationship Diagrams (ERDs) and Flyway migration notes.
- **`roadmap/`**: Detailed sprint-by-sprint objectives, deliverables, and acceptance criteria (`Sprint-01` through `Sprint-08`).

### 6. `.github/`
GitHub Actions CI/CD workflows:
- **`backend-ci.yml`**: Maven build and test automation on pull requests and main pushes.
- **`frontend-ci.yml`**, **`docker-build.yml`**, **`codeql.yml`**: Modular pipeline specifications.

---

## 🛠️ Contributor Development Workflow

### Prerequisites
- **JDK 21** installed and configured (`JAVA_HOME`).
- **Apache Maven 3.9+**.
- **Docker & Docker Compose**.

### Local Setup
1. Clone the repository:
   ```bash
   git clone https://github.com/madugundunanda-ui/Sentinel.git
   cd Sentinel
   ```
2. Copy environment variable settings:
   ```bash
   cp .env.example .env
   ```

### Monorepo Build Commands

- **Build Full Monorepo (Root Aggregator)**:
  ```powershell
  mvn clean verify
  ```
- **Build Backend Services Only**:
  ```powershell
  cd backend
  mvn clean install
  ```
- **Run Auth Service Locally**:
  ```powershell
  .\scripts\run-auth-service.ps1
  ```
- **Run Infrastructure with Docker Compose**:
  ```powershell
  docker compose -f infrastructure/docker/docker-compose.yml --env-file .env up --build
  ```

---

## 🌿 Git Branching & Pull Request Guidelines

1. **Branch Naming**:
   - Features: `feature/sprint-<number>-<feature-description>`
   - Bugfixes: `fix/<issue-description>`
   - Documentation: `docs/<doc-title>`
2. **Commit Conventions**:
   Follow Conventional Commits format (`feat:`, `fix:`, `docs:`, `refactor:`, `test:`).
3. **Pull Requests**:
   All PRs targeting `main` must pass the GitHub Actions CI build (`backend-ci.yml`) prior to merging.
