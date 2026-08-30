# Sentinel — Final Business Defense & Economics

## Overview
This document explains Sentinel's business value, decision policies, and operational impact for business judges and product evaluators.

---

## 💼 Business Defense Breakdown

### 1. Who is the Primary User?
Payment Risk Operations Teams, Fraud Analysts, and Security Operations Center (SOC) personnel managing checkout security for payment gateways and merchants.

### 2. What Business Problem Does Sentinel Solve?
Sentinel eliminates the dual cost problem in payment ecosystems:
- **Direct Fraud Losses**: Undetected fraud rings cause chargebacks ($C_{\text{FN}} = \text{₹6,800}$).
- **Customer Churn**: Over-aggressive fraud rules block legitimate checkouts ($C_{\text{FP}} = \text{₹1,200}$).

### 3. What Does APPROVE Mean?
`APPROVE` ($p < 0.25$) passes nominal checkouts instantly with zero customer friction and ₹0 expected business loss, maximizing checkout conversion.

### 4. What Does REVIEW Mean?
`REVIEW` ($0.25 \le p < 0.50$) routes ambiguous transactions to a human-in-the-loop analyst queue, spending ₹400 on manual investigation to avoid losing ₹1,200 in false-block customer friction.

### 5. What Does BLOCK Mean?
`BLOCK` ($p \ge 0.50$) mitigates high-risk fraud rings automatically, stopping ₹6,800 chargeback losses per incident and creating active SOC incidents.

### 6. Why is a False Positive Expensive ($C_{\text{FP}} = \text{₹1,200}$)?
Blocking a legitimate user destroys merchant transaction margin, incurs customer support overhead, and causes long-term customer churn.

### 7. Why is a False Negative Expensive ($C_{\text{FN}} = \text{₹6,800}$)?
Allowing fraud results in chargeback processing penalties, card network fines, loss of principal, and regulatory scrutiny.

### 8. Why Shouldn't Every Suspicious Transaction Be Blocked?
Blocking every suspicious transaction creates massive false positive friction. A 3-tier policy model with an intermediate `REVIEW` buffer balances customer conversion against risk mitigation.

### 9. How Does Analyst Workload Improve?
Explainable AI attributions ('Why This Decision?') and SVG relationship graphs allow fraud analysts to inspect evidence in seconds, reducing Mean-Time-To-Resolution (MTTR).

### 10. How Does Incident Response Improve?
Sentinel bridges risk scoring directly into SOC incident management (`/incidents`), eliminating communication delays between fraud risk teams and security operations.

### 11. How Could a Payment Gateway (e.g. Razorpay) Integrate It?
Via webhooks or streaming Kafka topics (`sentinel.financial.events`), invoking `risk-service` REST endpoints to receive real-time risk scores and decision recommendations.
