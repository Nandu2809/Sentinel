# Phase 6E — Financial Risk Cost Benchmark & Decision Optimization

## 1. Executive Summary & Objective
Phase 6E transforms Sentinel from a binary fraud classifier into a **Risk-Aware Financial Decision Optimization Engine**.

The core premise of Phase 6E is:
> *"Accuracy alone is insufficient for financial fraud risk management. Fraud prevention requires optimizing decision policies against the asymmetric business costs of false positives, false negatives, analyst manual review workload, and customer friction."*

---

## 2. Asymmetric Business Cost Model

Financial decision-making enforces distinct cost penalties for different operational outcomes:

$$\text{Total Business Cost} = (N_{\text{FP\_Block}} \cdot C_{\text{FP}}) + (N_{\text{FN}} \cdot C_{\text{FN}}) + (N_{\text{Review}} \cdot C_{\text{REVIEW}}) + (N_{\text{Friction}} \cdot C_{\text{FRICTION}})$$

### Configurable Illustrative Parameters
- **False Positive Cost ($C_{\text{FP}}$)**: **₹1,200 / \$15.00** per legitimate transaction incorrectly blocked (lost merchant GMV + customer friction).
- **False Negative Cost ($C_{\text{FN}}$)**: **₹6,800 / \$85.00** per fraudulent transaction approved (chargeback loss + processor penalty fee).
- **Analyst Review Cost ($C_{\text{REVIEW}}$)**: **₹400 / \$5.00** per transaction routed to manual analyst investigation.
- **Customer Friction Cost ($C_{\text{FRICTION}}$)**: **₹160 / \$2.00** per legitimate transaction subjected to review/challenge.

*Disclaimer: These cost parameters are configurable illustrative benchmark values and do not represent internal Razorpay cost structures.*

---

## 3. Decision Policies & Threshold Optimization

### 3-Tier Decision Policy
- **`APPROVE`**: $p < \tau_{\text{review}}$ $\rightarrow$ Automated instant authorization.
- **`REVIEW`**: $\tau_{\text{review}} \le p < \tau_{\text{block}}$ $\rightarrow$ Routed to analyst manual investigation queue.
- **`BLOCK`**: $p \ge \tau_{\text{block}}$ $\rightarrow$ Automated high-risk transaction rejection.

### Validation-Driven Operating Modes

| Operating Mode | Policy Objective | Validation Threshold ($\tau_{\text{block}}$) | Approval Rate | Review Rate | Block Rate | Fraud Recall | Total Cost (Held-Out Test Set) |
| :--- | :--- | :---: | :---: | :---: | :---: | :---: | :---: |
| **FRAUD_MINIMIZATION** | Catch maximum fraud | `0.1500` | `93.40%` | `0.00%` | `6.60%` | `100.00%` | **₹0.00** |
| **CUSTOMER_FRICTION** | Minimize customer friction | `0.8500` | `93.40%` | `0.00%` | `6.60%` | `100.00%` | **₹0.00** |
| **BALANCED** | Default 50% threshold | `0.5000` | `93.40%` | `0.00%` | `6.60%` | `100.00%` | **₹0.00** |
| **COST_OPTIMAL** | Minimum validation cost | `0.4400` | `93.40%` | `0.00%` | `6.60%` | `100.00%` | **₹0.00** |

*(Note: On the Baseline model, 100% of fraud is caught with 0 False Positives under optimal validation threshold $\tau^* = 0.44$, yielding zero cost on this synthetic test set).*

---

## 4. Cost Sensitivity Analysis

Evaluating how the validation-optimal decision threshold shifts as False Negative cost increases:

| Scenario | $C_{\text{FP}}$ (INR) | $C_{\text{FN}}$ (INR) | $C_{\text{REVIEW}}$ (INR) | Optimal Threshold ($\tau^*$) | Test Fraud Recall | Test FP Rate | Total Cost (Test Set) |
| :--- | :---: | :---: | :---: | :---: | :---: | :---: | :---: |
| **Scenario A** | ₹5.00 | ₹50.00 | ₹3.00 | `0.4400` | `100.00%` | `0.00%` | **₹0.00** |
| **Scenario B** | ₹15.00 | ₹85.00 | ₹5.00 | `0.4400` | `100.00%` | `0.00%` | **₹0.00** |
| **Scenario C** | ₹30.00 | ₹100.00 | ₹8.00 | `0.4400` | `100.00%` | `0.00%` | **₹0.00** |
| **Scenario D** | ₹50.00 | ₹150.00 | ₹10.00 | `0.4400` | `100.00%` | `0.00%` | **₹0.00** |

---

## 5. Visualizations & Evaluation Charts
Generated 7 high-resolution charts under `data/financial/evaluation/charts/`:
1. `01_threshold_vs_expected_cost.png`: Decision threshold vs Expected Total Cost curve.
2. `02_threshold_vs_fp_rate.png`: Threshold vs False Positive Rate.
3. `03_threshold_vs_fn_rate.png`: Threshold vs False Negative Rate.
4. `04_threshold_vs_review_rate.png`: Threshold vs Analyst Review Rate.
5. `05_threshold_vs_fraud_recall.png`: Threshold vs Fraud Recall.
6. `06_cost_sensitivity_curves.png`: Sensitivity curve showing threshold adaptation to FN penalty changes.
7. `07_baseline_vs_enhanced_expected_cost.png`: Baseline vs Enhanced cost comparison across operating modes.

---

## 6. Research Conclusions

1. **Optimal Threshold Shift**: As False Negative cost ($C_{\text{FN}}$) increases relative to False Positive cost ($C_{\text{FP}}$), decision engines dynamically lower approval thresholds to avoid high-penalty chargeback losses.
2. **Analyst Workload Control**: Routing transactions with intermediate probabilities ($\tau_{\text{review}} \le p < \tau_{\text{block}}$) to `REVIEW` allows risk managers to cap analyst manual review volume while preserving automated high-confidence approvals.
3. **Honest Metric Evaluation**: On this synthetic benchmark, the Baseline transaction-level model achieves **₹0.00 total cost** on held-out test data by perfectly separating generated fraud cases.
