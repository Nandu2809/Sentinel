# Sentinel Phase 8 — Emergency Demo Fallback Plan

This document defines emergency diagnostic procedures, non-destructive fixes, and safe fallbacks for presentation failures during live judge demos.

---

## 🚨 Emergency Matrix: Symptom → Diagnosis → Safe Fix → Fallback

| Component | Symptom | Root Cause Diagnosis | Safe Fix | Honest Fallback |
|---|---|---|---|---|
| **Frontend** | Angular UI on `:4200` not loading | Dev server stopped or crashed | Run `npx ng serve --host 0.0.0.0 --port 4200` | Access production build files in `dist/sentinel` via static web server |
| **API Gateway** | `HTTP 502 Bad Gateway` on `/api/*` | Gateway container down or route misconfigured | Run `docker compose restart gateway-service` | Call `risk-service` directly on `http://localhost:8084` with JWT token |
| **AI Engine** | Risk evaluation returns `HTTP 500` or timeout | Python FastAPI container on `:8000` down | Run `docker compose restart ai-engine` | Use Java fallback heuristic rules in `MultiFactorRiskCalculator.java` |
| **Kafka Bus** | Alerts not appearing in SOC UI | Kafka or Zookeeper container restarting | Run `docker compose restart kafka zookeeper` | Refresh database directly; `risk-service` falls back to direct DB persistence |
| **PostgreSQL** | Database connection refused on `:5432` | Postgres container stopped | Run `docker compose restart postgres` | Inspect logged HTTP JSON evaluation payloads in browser console |
| **Mailpit** | Email notification not visible on `:8025` | Mailpit container down | Run `docker compose restart mailpit` | Show Incident record in SOC UI (`/incidents`) proving alert generation |
| **Browser CDP** | Playwright / Browser automation connection timeout | Chrome CDP port 9222 unresponsive | Refresh browser tab manually or launch standard Chrome window | Present live demo manually in standard Web Browser |
| **Scenario Failure** | Unexpected Risk Score output on Scenario C | Transient graph cache state | Execute dataset soft reset via `/api/v1/financial-risk/reset` | Use pre-seeded scenario endpoints (`POST /api/v1/financial-risk/evaluate`) |

---

## 📌 Golden Rule for Fallbacks
> **Never fake or hardcode a result**. If an infrastructure component fails during a presentation, apply the safe fix or explain the exact architectural fault tolerance mechanism in place.
