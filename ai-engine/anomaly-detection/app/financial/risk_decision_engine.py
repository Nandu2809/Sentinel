"""
Sentinel Phase 6E — Risk-Aware Financial Decision Engine

Transforms ML probability predictions into risk-optimized business decisions:
- APPROVE (p < t_review)
- REVIEW  (t_review <= p < t_block)
- BLOCK   (p >= t_block)

Calculates total financial cost incorporating asymmetric business impact:
- False Positive Cost (C_FP)
- False Negative Cost (C_FN)
- Manual Investigation Cost (C_REVIEW)
- Customer Friction Cost (C_FRICTION)
"""

import math

class RiskDecisionEngine:
    """Configurable risk decision engine evaluating business costs & decision policies."""

    def __init__(self, false_positive_cost=15.0, false_negative_cost=85.0,
                 review_cost=5.0, customer_friction_cost=2.0,
                 t_review=0.30, t_block=0.70):
        self.c_fp = float(false_positive_cost)
        self.c_fn = float(false_negative_cost)
        self.c_review = float(review_cost)
        self.c_friction = float(customer_friction_cost)
        self.t_review = float(t_review)
        self.t_block = float(t_block)

    def evaluate_transaction(self, probability, actual_label, feature_dict=None):
        """
        Evaluate a single transaction prediction against business policy.
        """
        prob = float(probability)
        actual = int(actual_label)
        risk_score = round(prob * 100.0, 2)

        # Apply 3-tier decision policy
        if prob < self.t_review:
            decision = "APPROVE"
        elif prob < self.t_block:
            decision = "REVIEW"
        else:
            decision = "BLOCK"

        is_fp = False
        is_fn = False
        cost = 0.0
        reasons = []

        if actual == 0:  # Legitimate Transaction
            if decision == "BLOCK":
                is_fp = True
                cost = self.c_fp + self.c_friction
                reasons.append("FALSE_POSITIVE_HIGH_RISK_BLOCK")
            elif decision == "REVIEW":
                is_fp = True
                cost = self.c_review + self.c_friction
                reasons.append("LEGITIMATE_ACCOUNT_MANUAL_REVIEW")
            else:
                reasons.append("LOW_RISK_AUTOMATED_APPROVAL")
        else:  # Fraudulent Transaction
            if decision == "APPROVE":
                is_fn = True
                cost = self.c_fn
                reasons.append("MISSED_FRAUD_AUTOMATED_APPROVAL")
            elif decision == "REVIEW":
                cost = self.c_review
                reasons.append("SUSPECTED_FRAUD_MANUAL_REVIEW")
            else:  # BLOCK
                cost = 0.0  # Successfully blocked fraud
                reasons.append("CONFIRMED_FRAUD_AUTOMATED_BLOCK")

        # Extract domain feature reasons if available
        if feature_dict:
            if feature_dict.get("velocity1h", 0) >= 5:
                reasons.append("HIGH_TRANSACTION_VELOCITY")
            if feature_dict.get("sharedDeviceAccountCount", 1) > 2:
                reasons.append("SHARED_DEVICE_ACTIVITY")
            if feature_dict.get("sharedIpAccountCount", 1) > 4:
                reasons.append("SHARED_IP_BURST")
            if feature_dict.get("sharedPaymentAccountCount", 1) > 2:
                reasons.append("PAYMENT_REFERENCE_REUSE")

        return {
            "decision": decision,
            "riskScore": risk_score,
            "probability": round(prob, 4),
            "is_false_positive": is_fp,
            "is_false_negative": is_fn,
            "business_cost": round(cost, 2),
            "reasonCodes": reasons
        }

    def evaluate_dataset_policy(self, probabilities, actual_labels, t_review=None, t_block=None):
        """
        Evaluate full dataset predictions under decision policy and calculate aggregate metrics & costs.
        """
        t_rev = self.t_review if t_review is None else float(t_review)
        t_blk = self.t_block if t_block is None else float(t_block)

        n = len(probabilities)
        if n == 0:
            return {}

        approve_cnt = 0
        review_cnt = 0
        block_cnt = 0

        fp_cnt = 0
        fn_cnt = 0
        tp_cnt = 0
        tn_cnt = 0

        total_fp_cost = 0.0
        total_fn_cost = 0.0
        total_review_cost = 0.0
        total_friction_cost = 0.0

        for p, y in zip(probabilities, actual_labels):
            p_val = float(p)
            y_val = int(y)

            if p_val < t_rev:
                dec = "APPROVE"
                approve_cnt += 1
            elif p_val < t_blk:
                dec = "REVIEW"
                review_cnt += 1
            else:
                dec = "BLOCK"
                block_cnt += 1

            if y_val == 0:  # Legit
                if dec == "APPROVE":
                    tn_cnt += 1
                elif dec == "REVIEW":
                    fp_cnt += 1
                    total_review_cost += self.c_review
                    total_friction_cost += self.c_friction
                else:  # BLOCK
                    fp_cnt += 1
                    total_fp_cost += self.c_fp
                    total_friction_cost += self.c_friction
            else:  # Fraud
                if dec == "APPROVE":
                    fn_cnt += 1
                    total_fn_cost += self.c_fn
                elif dec == "REVIEW":
                    tp_cnt += 1
                    total_review_cost += self.c_review
                else:  # BLOCK
                    tp_cnt += 1

        total_cost = total_fp_cost + total_fn_cost + total_review_cost + total_friction_cost
        cost_per_1000 = (total_cost / n) * 1000.0 if n > 0 else 0.0

        fraud_total = sum(actual_labels)
        fraud_recall = tp_cnt / fraud_total if fraud_total > 0 else 0.0
        precision = tp_cnt / (tp_cnt + fp_cnt) if (tp_cnt + fp_cnt) > 0 else 0.0

        return {
            "t_review": round(t_rev, 4),
            "t_block": round(t_blk, 4),
            "total_transactions": n,
            "approve_count": approve_cnt,
            "review_count": review_cnt,
            "block_count": block_cnt,
            "approval_rate": round(approve_cnt / n, 4),
            "review_rate": round(review_cnt / n, 4),
            "block_rate": round(block_cnt / n, 4),
            "fp_count": fp_cnt,
            "fn_count": fn_cnt,
            "tp_count": tp_cnt,
            "tn_count": tn_cnt,
            "fraud_recall": round(fraud_recall, 4),
            "precision": round(precision, 4),
            "fp_cost": round(total_fp_cost, 2),
            "fn_cost": round(total_fn_cost, 2),
            "review_cost": round(total_review_cost, 2),
            "friction_cost": round(total_friction_cost, 2),
            "total_cost": round(total_cost, 2),
            "cost_per_1000": round(cost_per_1000, 2)
        }
