# Sentinel — Final Live Demo Screen Order

## Overview
This document specifies the exact screen navigation sequence to avoid unnecessary UI clicks during live hackathon judging.

---

## 🖥️ Screen Navigation Sequence (11 Steps)

1. **Login Workspace (`/login`)**: Display dark-mode login interface with pre-filled administrator credentials (`admin@sentinel.io`).
2. **Financial Command Center (`/financial-risk`)**: Land on main dashboard header displaying system badges (`SYSTEM: LIVE`, `KAFKA: CONNECTED`, `GRAPH: ONLINE`).
3. **Scenario A Execution (`APPROVE`)**: Click `SCENARIO A: LEGITIMATE (APPROVE)` $\rightarrow$ row `tx_legit_10291` prepends in green.
4. **Scenario B Execution (`REVIEW`)**: Click `SCENARIO B: AMBIGUOUS (REVIEW)` $\rightarrow$ row `tx_ambig_44120` prepends in amber; `HUMAN-IN-THE-LOOP` indicator banner activates.
5. **Scenario C Execution (`BLOCK`)**: Click `SCENARIO C: HIGH-RISK RING (BLOCK)` $\rightarrow$ row `tx_ring_88291` prepends in red.
6. **Explainable AI Panel Inspection**: Focus cursor on `EXPLAINABLE AI — WHY THIS DECISION?` showing 4 attribution signals.
7. **SVG Relationship Topology Graph**: Hover/click red device node showing 8 linked user accounts and 3 card token references.
8. **Risk Policy Threshold Simulator**: Slide `simBlockThreshold` to `0.70` showing dynamic metric recalculation.
9. **Expected Business Cost Curve**: Point to visual SVG cost curve highlighting optimal threshold $\tau^* = 0.50$.
10. **SOC Incident Bridge (`/incidents`)**: Click `🚨 OPEN INCIDENT WORKSPACE` button $\rightarrow$ transitions to `/incidents` workspace displaying active record `INC-2026-1000`.
11. **Threat Hunting Workbench (`/threat-hunting`)**: Click `🔍 THREAT HUNTING WORKBENCH` button $\rightarrow$ transitions to query workbench with pre-populated IP/device filter.
