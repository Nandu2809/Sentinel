# Sentinel — Risk Economics & Business Cost Model Defense

## Executive Summary
This document explains the financial loss optimization model used by Sentinel's 3-tier decision engine.

> ⚠️ **DISCLOSURE**: Cost parameter values ($C_{\text{FP}} = \text{₹1,200}$, $C_{\text{FN}} = \text{₹6,800}$, $C_{\text{REVIEW}} = \text{₹400}$, $C_{\text{FRICTION}} = \text{₹160}$) are **synthetic illustrative parameters** used to benchmark cost-aware decision logic. They do not represent proprietary Razorpay financial figures.

---

## The Business Cost Equation

Total expected business cost $C_{\text{total}}(\tau)$ is formulated as:

$$C_{\text{total}}(\tau) = N_{\text{FP}}(\tau) \cdot C_{\text{FP}} + N_{\text{FN}}(\tau) \cdot C_{\text{FN}} + N_{\text{REVIEW}}(\tau) \cdot C_{\text{REVIEW}} + N_{\text{FRICTION}}(\tau) \cdot C_{\text{FRICTION}}$$

### Cost Components Breakdown

1. **False Positive Block Penalty ($C_{\text{FP}} = \text{₹1,200}$)**:
   - Blocking a legitimate checkout loses merchant transaction margin and causes long-term customer churn.
2. **False Negative Chargeback Loss ($C_{\text{FN}} = \text{₹6,800}$)**:
   - Allowing a fraudulent transaction incurs chargeback processing fees, card network penalties, and loss of principal.
3. **Analyst Manual Investigation Cost ($C_{\text{REVIEW}} = \text{₹400}$)**:
   - Hourly wage and overhead cost of a fraud analyst manually inspecting transaction evidence in SOC queue.
4. **Customer Friction Cost ($C_{\text{FRICTION}} = \text{₹160}$)**:
   - Minor friction cost associated with Step-Up 2FA / OTP challenges during intermediate verification.

---

## 3-Tier Policy Threshold Optimization

Using empirical validation tuning on `validation.csv`, Sentinel computes optimal boundaries:

```
    APPROVE (Instant Pass)           REVIEW (Analyst Queue)           BLOCK (Mitigate)
◄───────────────────────────────┼───────────────────────────────┼───────────────────────────────►
      Risk Score < 25                 25 ≤ Risk Score < 50               Risk Score ≥ 50
     (Risk Prob < 0.25)             (0.25 ≤ Risk Prob < 0.50)           (Risk Prob ≥ 0.50)
```

- **Why $\tau_{\text{block}} = 0.50$?**: Minimizes total expected cost by balancing $C_{\text{FN}}$ avoidance against $C_{\text{FP}}$ false blocks.
- **Why $\tau_{\text{review}} = 0.25$?**: Routes ambiguous transactions ($0.25 \le p < 0.50$) to manual review, incurring a small ₹400 investigation cost instead of risking a ₹6,800 chargeback or ₹1,200 false block.
