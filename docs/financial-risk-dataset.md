# Sentinel Synthetic Financial Risk Dataset Specification

## 1. Overview & Purpose
This document specifies the synthetic financial risk dataset foundation for **Sentinel Phase 6B** in alignment with the **Razorpay AI Builder Internship 2026 Track 2 (AI Risk Manager)** requirements.

The goal of this dataset is to provide a reproducible, seed-driven benchmark to evaluate models detecting **Coordinated Payment Abuse & Fraud-Rings**.

> **IMPORTANT PRIVACY & SECURITY STATEMENT**:
> *This dataset is synthetic and does not represent real Razorpay transaction data or live payment network records.*
> *It contains ZERO real payment credentials, card numbers, CVVs, OTPs, or passwords.*
> *All user identifiers, payment method references, device fingerprints, and IP addresses are synthetic, tokenized strings.*

---

## 2. Dataset Schema (`FinancialRiskEvent`)

The dataset schema matches the canonical Java record `com.sentinel.common.events.FinancialRiskEvent`:

| Field Name | Type | Description | Predictive Feature / Metadata |
| :--- | :--- | :--- | :--- |
| `transactionId` | String | Unique synthetic transaction identifier (`tx_legit_xxx`, `tx_fraud_xxx`) | Identifier |
| `userId` | String | Tokenized user account ID (`usr_norm_xxxx`, `usr_fraud_xxxx`) | Predictive Feature |
| `merchantId` | String | Tokenized merchant identifier (`merch_razorpay_xxx`) | Predictive Feature |
| `amount` | Double | Transaction amount in INR (₹50.00 – ₹95,000.00) | Predictive Feature |
| `currency` | String | Currency code (`INR`) | Predictive Feature |
| `timestamp` | ISO-8601 | Transaction event timestamp | Predictive Feature |
| `deviceId` | String | Device hardware fingerprint token (`dev_fingerprint_xxxx`) | Predictive Feature |
| `ipAddress` | String | Network IPv4 address | Predictive Feature |
| `location` | String | Geographic city/country location string | Predictive Feature |
| `paymentMethodRef` | String | Tokenized payment method reference (`pm_tok_xxxx`) | Predictive Feature |
| `accountAgeDays` | Integer | Account age in days at transaction time | Predictive Feature |
| `velocity1h` | Integer | Number of transactions attempted by user in prior 1 hour | Predictive Feature |
| `failedTxCount24h` | Integer | Number of failed transaction attempts in prior 24 hours | Predictive Feature |
| `sharedDeviceAccountCount` | Integer | Number of distinct user accounts linked to this device fingerprint | Predictive Feature |
| `sharedIpAccountCount` | Integer | Number of distinct user accounts linked to this IP address | Predictive Feature |
| `isFraud` | Integer | Binary ground-truth target label (`0` = Legitimate, `1` = Fraud) | **Ground Truth Target Only** |
| `fraudScenario` | String | Ground-truth attack category name | **Evaluation Metadata Only** |

---

## 3. Coordinated Abuse Fraud Scenarios

The synthetic generator (`scripts/financial_dataset_generator.py`) generates 5 realistic coordinated abuse patterns:

1. **`DEVICE_SHARING_RING`**:
   - Multiple distinct user accounts (`usr_fraud_ringA_...`) execute transactions using an identical device fingerprint (`dev_fraud_ring_A_...`).
   - High `sharedDeviceAccountCount` (6 to 18) and low `accountAgeDays` (1 to 14 days).

2. **`SHARED_IP_BURST`**:
   - Rapid burst of transactions originating from a shared proxy/VPN IP address (`203.0.113.x`) across multiple accounts.
   - High `sharedIpAccountCount` (12 to 35), high `velocity1h` (8 to 24), and elevated `failedTxCount24h`.

3. **`PAYMENT_REF_REUSE`**:
   - A single tokenized payment reference (`pm_tok_stolen_C_...`) is reused across multiple newly created accounts (`accountAgeDays` 0 to 5 days).

4. **`BEHAVIORAL_ANOMALY`**:
   - Sudden high-value transaction spike (₹35,000.00 – ₹95,000.00) on low-history accounts accompanied by high velocity.

5. **`COMBINED_RING`**:
   - Maximum severity multi-vector attack sharing device, IP, and payment reference simultaneously across 10–25 accounts with high transaction velocity.

---

## 4. Train / Validation / Test Split Methodology

To prevent temporal leakage and evaluate model generalization on unseen data:
- **Total Dataset Size**: 10,000 transactions.
- **Prevalence**: 92% Legitimate (9,200), 8% Fraudulent (800).
- **Split Ratios**:
  - **Training Set (`train.csv`)**: 70% (7,000 transactions)
  - **Validation Set (`validation.csv`)**: 10% (1,000 transactions)
  - **Held-Out Test Set (`test.csv`)**: 20% (2,000 transactions)
- **Split Execution**: Chronological ordering by timestamp. The final 2,000 transactions remain completely untouched during model development and threshold tuning.

---

## 5. Data Leakage Prevention

1. **Target Leakage**: `isFraud` and `fraudScenario` are strictly excluded from predictive model feature vectors.
2. **Temporal Leakage**: Features such as `velocity1h` and `failedTxCount24h` are computed using strictly backward-looking time windows prior to event timestamp.

---

## 6. Illustrative False-Positive Economic Cost Parameters

To satisfy Track 2's requirement for false-positive cost analysis, the following parameters are established for evaluation in Phase 6E:
- **False Positive Cost ($C_{\text{FP}}$)**: **₹1,200 / \$15.00** per legitimate transaction incorrectly blocked (lost GMV margin + customer friction).
- **False Negative Cost ($C_{\text{FN}}$)**: **₹6,800 / \$85.00** per fraudulent transaction missed (full chargeback loss + gateway penalty fee).
