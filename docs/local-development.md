# Sentinel — Complete Local Development Guide

This guide details how to configure, run, and test the **Sentinel Financial Risk Intelligence Platform** in **Local Development Mode** as well as **Dockerized Production Mode**.

---

## 🛠️ Environment Prerequisites Matrix

| Component | Required Version | Local Binary Location / Command | Status |
| :--- | :--- | :--- | :--- |
| **Java SDK** | Java 21 (LTS) | `java -version` (Temurin 21.0.12) | ✅ PASS |
| **Apache Maven** | Maven 3.9+ | `mvn -version` (3.9.12) | ✅ PASS |
| **Node.js / npm** | Node 18+ / npm 10+ | `node -v` (v24.11.1) / `npm -v` (11.6.2) | ✅ PASS |
| **Python** | Python 3.12 (Virtualenv) | `ai-engine/.venv` (`py -3.12 -m venv .venv`) | ✅ PASS |
| **Docker Engine** | Docker 24+ & Compose v2+ | `docker compose version` (v5.3.1) | ✅ PASS |

---

## 🚀 Execution Modes

### Mode A: Dockerized Full-Stack Execution (Containerized)

Use this mode for complete integration testing, presentations, and demo day.

1. **Copy Environment Reference**:
   ```powershell
   Copy-Item .env.example .env
   ```
2. **Compile Backend Services**:
   ```powershell
   mvn clean package -DskipTests
   ```
3. **Start All Stack Containers**:
   ```powershell
   docker compose -f infrastructure/docker/docker-compose.yml up -d --build
   ```
4. **Access Applications**:
   - Angular SOC Dashboard: `http://localhost:4200`
   - API Gateway: `http://localhost:8088`
   - Mailpit Webmail: `http://localhost:8025`
   - Grafana Dashboard: `http://localhost:3000` (`admin` / `admin`)

---

### Mode B: Local Component Development Mode (Host Execution)

Use this mode for step-by-step feature development and live debugging.

#### 1. Start Support Infrastructure Only
```powershell
docker compose -f infrastructure/docker/docker-compose.yml up -d postgres redis kafka zookeeper mailpit
```

#### Database & Networking Architecture:
- **Docker Container Mode**: Microservices communicate over internal bridge network `sentinel-net` using host `postgres:5432`, `kafka:29092`, `redis:6379`.
- **Local Host Mode**: Microservices executed directly (`mvn spring-boot:run`) fallback seamlessly to `localhost:5432` for PostgreSQL, `localhost:9092` for Kafka, and `localhost:6379` for Redis.
- **Multi-Schema Database Design**: All services share PostgreSQL database `sentinel_db` under dedicated schemas (`auth`, `monitoring`, `threat`, `risk`, `alert`, `report`). Flyway automatically creates and applies schema migrations upon service startup.

#### 2. Launch AI Engine
```powershell
# From project root:
.\scripts\run-ai-engine.ps1

# Or from ai-engine directory:
cd ai-engine
.\run.ps1
```
*(Runs FastAPI Python server on `http://localhost:8000`)*

#### 3. Launch Backend Microservices
Run individual services using Maven or scripts:
```powershell
# Auth Service (Port 8081):
.\scripts\run-auth-service.ps1

# Gateway Service (Port 8088):
cd backend/gateway-service && mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dserver.port=8088"

# Risk Service (Port 8084):
cd backend/risk-service && mvn spring-boot:run

# Alert Service (Port 8085):
cd backend/alert-service && mvn spring-boot:run
```

#### 4. Launch Angular Frontend Dev Server
```powershell
cd frontend/sentinel
npm install
npm run start
```
*(Runs Angular application on `http://localhost:4200` with HTTP proxy to Gateway `:8088`)*

---

## 🌐 Local Endpoint & Health Verification Table

| Service Name | Port | Health Endpoint | Purpose |
| :--- | :--- | :--- | :--- |
| **API Gateway** | `8088` | `http://localhost:8088/actuator/health` | Central API Gateway |
| **Auth Service** | `8081` | `http://localhost:8081/actuator/health` | User Auth & JWT Token Issuance |
| **Monitoring Service** | `8082` | `http://localhost:8082/actuator/health` | Telemetry & Audit Logging |
| **Threat Service** | `8083` | `http://localhost:8083/actuator/health` | WAF Rule Evaluation & Detectors |
| **Risk Service** | `8084` | `http://localhost:8084/actuator/health` | Financial Risk Decision Engine & Graph |
| **Alert Service** | `8085` | `http://localhost:8085/actuator/health` | Alert Escalation & Incident Management |
| **Report Service** | `8086` | `http://localhost:8086/actuator/health` | Analytics & Report Generation |
| **AI Engine** | `8000` | `http://localhost:8000/api/v1/ai/status` | Anomaly Detection & ML Model |
| **Mailpit** | `8025` | `http://localhost:8025` | Email Notification Mock Server |

---

## 🧪 Verification Commands

- **Backend Maven Tests**:
  ```powershell
  cd backend && mvn clean test
  ```
- **Angular Build Test**:
  ```powershell
  cd frontend/sentinel && npm run build
  ```
- **Python ML Tests**:
  ```powershell
  & ".\ai-engine\.venv\Scripts\python.exe" -m unittest tests/test_phase6f_financial.py
  ```
