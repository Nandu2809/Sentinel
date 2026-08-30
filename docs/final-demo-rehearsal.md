# Sentinel — Final Live Demo Rehearsal Checklist

## Pre-Demo Technical Checklist

- [ ] **Docker Containers Active**: `docker compose ps` verifies 14 healthy containers.
- [ ] **PostgreSQL Health**: `sentinel-postgres` responding on port 5432.
- [ ] **Redis Health**: `sentinel-redis` responding on port 6379.
- [ ] **Kafka & Zookeeper**: `sentinel-kafka` advertising `localhost:9092`.
- [ ] **Microservices Readiness**: `/actuator/health` returns `UP` on ports 8081, 8082, 8083, 8084, 8085, 8086.
- [ ] **Gateway Service**: `sentinel-gateway-service` routing requests on port 8088.
- [ ] **AI Engine Status**: `/api/v1/ai/status` returns status `healthy`.
- [ ] **Frontend Web Server**: `ng serve` active on `http://localhost:4200`.
- [ ] **Command Center Route**: `http://localhost:4200/financial-risk` loaded and responsive.

---

## 📋 Execution Steps Sequence

### 1. Initial State Inspection
- Verify header badges: `SYSTEM: LIVE`, `KAFKA: CONNECTED`, `GRAPH: ONLINE`.

### 2. Scenario A Execution
- Click `SCENARIO A: LEGITIMATE (APPROVE)`.
- Confirm green row `tx_legit_10291` prepended to stream table.
- Confirm Risk Score = `12 / 100` and Decision = `APPROVE`.

### 3. Scenario B Execution
- Click `SCENARIO B: AMBIGUOUS (REVIEW)`.
- Confirm amber row `tx_ambig_44120` prepended.
- Confirm Risk Score = `54 / 100`, Decision = `REVIEW`, and `HUMAN-IN-THE-LOOP` banner displayed.

### 4. Scenario C Execution
- Click `SCENARIO C: HIGH-RISK RING (BLOCK)`.
- Confirm red row `tx_ring_88291` prepended.
- Confirm Risk Score = `87 / 100`, Decision = `BLOCK`.
- Inspect red SVG Relationship Topology Graph (8 accounts shared on device fingerprint).

### 5. Explainable AI & Policy Simulator Inspection
- View `EXPLAINABLE AI — WHY THIS DECISION?` panel (4 attribution signals).
- Move `simBlockThreshold` slider to `0.70` to demonstrate simulation recalculation.

### 6. Incident Workspace Bridge
- Click `🚨 OPEN INCIDENT WORKSPACE (/incidents)`.
- Confirm transition to `/incidents` displaying active incident record `INC-2026-1000`.
