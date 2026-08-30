# Sentinel — Business Value Analysis

## Overview
This document evaluates the financial and operational impact of deploying Sentinel in high-throughput payment ecosystems.

---

## 📊 Business Value Comparison

### WITHOUT SENTINEL (Legacy Fraud Rules / Binary Classifiers)
```
Transaction Event ──> Static Rules / Binary ML ──> BLOCK or ALLOW
```
- **False Positive Friction**: Legitimate users on corporate IPs get blocked ($C_{\text{FP}} = \text{₹1,200}$ per incident), causing customer churn.
- **Missed Evasive Fraud**: Multi-account fraud rings pass single-transaction checks, causing chargeback losses ($C_{\text{FN}} = \text{₹6,800}$ per incident).
- **Analyst Fatigue**: Fraud analysts operate in isolated tools without graph relationship context.

---

### WITH SENTINEL (Integrated AI Risk Manager)
```
Transaction Event ──> Topology Graph ──> ML Risk ──> Cost Optimization ──> APPROVE / REVIEW / BLOCK ──> SOC Incident
```
- **Reduced False Positives**: Graph features disambiguate corporate subnets, reducing false blocks on corporate users from **$6 \rightarrow 0$ FP**.
- **Improved Evasive Fraud Recall**: Graph topology catches multi-account device sharing, increasing evasive fraud recall from **$95.65\% \rightarrow 100.0\%$ (+4.35%)**.
- **Optimized Financial Loss**: Setting $\tau_{\text{block}} = 0.50$ and $\tau_{\text{review}} = 0.25$ minimizes total expected business cost $C_{\text{total}}(\tau)$.
- **Analyst Productivity**: Human-in-the-Loop `REVIEW` queue ($C_{\text{REVIEW}} = \text{₹400}$) isolates ambiguous events, while direct bridges to `/incidents` reduce Mean-Time-To-Resolution (MTTR).
