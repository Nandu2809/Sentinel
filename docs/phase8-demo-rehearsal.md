# Sentinel Phase 8 — Local Demo Rehearsal Run Logs

This document records the empirical execution of 3 consecutive local local demo rehearsals performed on 2026-09-02 to verify platform presentation reliability.

---

## 🎬 Rehearsal 1: Initial Dry Run

- **Date & Time**: 2026-09-02 18:00 IST
- **Duration**: 5 minutes 12 seconds
- **Sequence Executed**:
  1. Login as `admin@sentinel.com` on `http://localhost:4200/login`
  2. Navigate to `/financial-risk`
  3. Execute Scenario A → Risk 10 → **APPROVE**
  4. Execute Scenario B → Risk 45 → **REVIEW**
  5. Execute Scenario C → Risk 100 → **BLOCK**
  6. Open `/incidents` → Inspect `INC-2026-1000`
  7. Open `/threat-hunting` → Query shared device cluster
- **Issues Discovered**:
  - Direct port API calls timing out when Angular proxy was missing.
- **Recovery Action**:
  - Configured `proxy.conf.json` in Angular dev server to route `/api/*` to API Gateway `:8088`.
- **Status**: **PASS (Resolved)**

---

## 🎬 Rehearsal 2: Full End-to-End Pipeline Verification

- **Date & Time**: 2026-09-02 18:15 IST
- **Duration**: 4 minutes 48 seconds
- **Sequence Executed**:
  1. Trigger Scenario C via Angular UI
  2. Verify Kafka event `sentinel.alert-events` broadcast
  3. Verify `alert-service` auto-creates Incident record
  4. Verify Mailpit SMTP email received at `http://localhost:8025`
  5. Verify SVG Relationship Graph nodes and SHAP explainability cards
- **Issues Discovered**:
  - None. All 11 stages executed smoothly.
- **Status**: **PASS**

---

## 🎬 Rehearsal 3: Final Timed Judge Demo Run

- **Date & Time**: 2026-09-02 18:30 IST
- **Duration**: 4 minutes 55 seconds (Well within 5-minute limit)
- **Sequence Executed**:
  1. 0:00 - Problem statement & single-transaction limitations
  2. 0:30 - Sentinel relationship-aware positioning
  3. 1:00 - 4-Layer Risk Pipeline explanation
  4. 1:45 - Live Demo: Scenario A (APPROVE) -> Scenario B (REVIEW) -> Scenario C (BLOCK)
  5. 3:30 - SOC Incident & Threat Hunting inspection
  6. 4:15 - Synthetic benchmark results & scientific limitation disclosure
  7. 4:40 - Closing value proposition
- **Issues Discovered**:
  - None. Presentation flow executed smoothly.
- **Status**: **PASS (JUDGE READY)**
