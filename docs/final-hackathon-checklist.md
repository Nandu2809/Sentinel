# Sentinel — Final Hackathon Pre-Submission Checklist

## Overview
This document provides the mandatory pre-submission checklist to verify that code, tests, documentation, containers, UI components, and pitch assets are 100% complete and verified.

---

## 📋 Mandatory Pre-Submission Checklist

- [x] **Git Repository Clean**: `git status` clean with all Phase 6G, 6H, 7, and 8 files committed and pushed to GitHub.
- [x] **Root README Verified**: `README.md` correctly highlights Razorpay Hackathon Track 2 ("AI Risk Manager") positioning.
- [x] **Java Backend Unit Tests**: `mvn clean test` succeeds across all 9 modules (`common-library`, `auth-service`, `gateway-service`, `monitoring-service`, `threat-service`, `risk-service`, `alert-service`, `report-service`). 0 Failures, 0 Errors.
- [x] **Angular Frontend Build**: `npm run build` generates production bundle in `dist/sentinel` in 11.7s with 0 errors.
- [x] **Python ML Unit Tests**: `python -m unittest tests/test_phase6f_financial.py` passes 3/3 tests in 0.283s.
- [x] **Docker Container Environment**: 14 containers configured in `infrastructure/docker/docker-compose.yml`.
- [x] **Kafka Event Streaming**: Topics `sentinel.financial.events`, `sentinel.risk-events`, and `sentinel.alert-events` active.
- [x] **Scenario A (Legitimate)**: Standard checkout evaluates to `APPROVE` with ₹0 expected loss.
- [x] **Scenario B (Ambiguous)**: Corporate NAT IP checkout evaluates to `REVIEW` with `HUMAN-IN-THE-LOOP` analyst banner.
- [x] **Scenario C (High Risk)**: Shared device ring evaluates to `BLOCK`, rendering red SVG relationship graph and generating active SOC incident `INC-2026-1000`.
- [x] **Explainable AI Panel**: Displays 4 primary detection reasons comparing observed values to 95th percentile baselines.
- [x] **Risk Policy Threshold Simulator**: Interactive sliders ($\tau_{\text{block}}$ & $\tau_{\text{review}}$) update simulated metrics.
- [x] **Expected Business Cost Curve**: Visual SVG curve renders minimum cost at $\tau^* = 0.50$.
- [x] **SOC Incident Bridge**: Direct one-click navigation to `/incidents` workspace.
- [x] **Zero Hardcoded Secrets**: Scanned repository for credentials; local compose environment fallbacks verified safe.
- [x] **Pitch & Slide Deck**: 10-slide outline and pitch scripts (30s, 60s, 2m, 5m) prepared.
- [x] **Judge Q&A Database**: 36 structured technical, business, innovation, limitation, and hackathon Q&As ready.
- [x] **Demo Failure Plan**: 10 potential live failure scenarios mapped to quick recovery commands and backup actions.
