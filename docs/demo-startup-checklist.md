# Sentinel — Live Demo Startup Checklist

## Overview
This document provides the exact sequence of commands to reliably spin up the complete Sentinel platform for live hackathon demonstration.

---

## 🚀 Step-by-Step Demo Startup Checklist

### Step 1: Environment & Prerequisites Verification
```bash
# Check Docker and Docker Compose
docker --version
docker compose version

# Check Java & Node versions
java -version    # Required: OpenJDK 21
node -v          # Required: Node v18+
```

### Step 2: Launch Platform Infrastructure via Docker
```bash
cd infrastructure/docker
docker compose up -d

# Verify all 14 containers are running
docker compose ps
```

### Step 3: Verify Container Health Endpoints
```bash
# Gateway Health
curl http://localhost:8088/actuator/health

# Risk Service Health
curl http://localhost:8084/actuator/health

# AI Engine Status
curl http://localhost:8000/api/v1/ai/status
```

### Step 4: Launch Angular SOC Command Center UI
```bash
cd ../../frontend/sentinel

# Build production distribution
npm run build

# Option A: Serve via ng serve for live demo
npx ng serve --port 4200 --open

# Option B: Access pre-built bundle
# Open http://localhost:4200 in browser
```

### Step 5: Verify Critical UI Routes
- Navigate to `http://localhost:4200/financial-risk` (Financial Command Center)
- Navigate to `http://localhost:4200/incidents` (SOC Incident Response)
- Navigate to `http://localhost:4200/threat-hunting` (Threat Hunting Workbench)
