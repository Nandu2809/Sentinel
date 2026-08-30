# Sentinel — Judge Storyline & Narrative Flow

## Overview
This document structures the presentation narrative so it reads as a purposeful problem-solving journey rather than a technical feature list.

---

## 📖 The Narrative Arc

```
                      1. THE PROBLEM
            (Chargebacks vs False Block Friction)
                          │
                          ▼
            2. WHY EXISTING APPROACHES FAIL
              (Rule Engines & Binary Models)
                          │
                          ▼
                  3. INTRODUCING SENTINEL
               (AI Risk Management Platform)
                          │
                          ▼
                4. HOW IT WORKS IN DEPTH
               (Telemetry → Graph → ML → Cost)
                          │
         ┌────────────────┴────────────────┐
         ▼                                 ▼
   5. WHY GRAPH + AI              6. WHY COST DECISIONS
(Topology Ring Signals)         (3-Tier Policy Thresholds)
         │                                 │
         └────────────────┬────────────────┘
                          ▼
                  7. LIVE SYSTEM DEMO
               (Scenarios A, B, C Walkthrough)
                          │
                          ▼
                  8. BUSINESS IMPACT
               (Avoided Loss & Friction Reduction)
                          │
                          ▼
              9. LIMITATIONS & PRODUCTION FUTURE
              (Synthetic Disclosures & Roadmap)
```

---

## 🎙️ Storyline Section Breakdown

### 1. Problem Definition
Payment platforms process millions of checkouts daily. They face a constant battle: chargebacks from undetected fraud cause massive financial losses, while over-aggressive fraud rules block legitimate checkouts, alienating customers and destroying brand trust.

### 2. Why Existing Approaches Fail
- **Rule Engines**: Static thresholds (e.g. `amount > ₹50k`) are rigid, easy for fraudsters to bypass, and generate high false positives.
- **Binary ML Classifiers**: Output a raw probability ($p = 0.85$) without explaining feature attributions, inspecting entity relationships, or assessing business costs.

### 3. Introducing Sentinel
Sentinel is an explainable, relationship-aware, cost-sensitive AI Risk Management platform designed specifically for Razorpay Track 2.

### 4. How It Works
Financial transaction envelopes (`FinancialRiskEvent`) stream through Kafka into `risk-service`. Sentinel extracts 34 behavioral and graph features, scores risk, optimizes business costs, and emits alerts to `alert-service` and `incident-service`.

### 5. Why Graph + AI
Coordinated fraud rings use device pools and proxy subnets. Graph features detect node density and multi-account sharing, improving evasive fraud recall from 95.65% to 100% while preventing false blocks on corporate subnets.

### 6. Why Cost-Aware Decisions
A false block costs ₹1,200 in margin and churn. A missed fraud costs ₹6,800 in chargebacks. Setting $\tau_{\text{block}} = 0.50$ and $\tau_{\text{review}} = 0.25$ minimizes total expected business loss.

### 7. Live System Demo
Demonstrate live execution of Scenario A (`APPROVE`), Scenario B (`REVIEW` with Human-in-the-Loop), and Scenario C (`BLOCK` with active SOC Incident creation).

### 8. Business Impact & Honest Disclosures
Highlight avoided loss metrics, explain anti-leakage scientific discipline, disclose synthetic dataset limitations, and present a realistic production roadmap.
