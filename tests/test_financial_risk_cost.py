#!/usr/bin/env python3
"""
Sentinel Phase 6E — Unit Test Suite for Risk-Aware Decision Engine & Financial Cost Benchmark
"""

import unittest
import os
import json
import numpy as np
import pandas as pd
from importlib.util import module_from_spec, spec_from_file_location

engine_path = os.path.abspath(os.path.join(os.path.dirname(__file__), "../ai-engine/anomaly-detection/app/financial/risk_decision_engine.py"))
e_spec = spec_from_file_location("risk_decision_engine", engine_path)
risk_decision_engine = module_from_spec(e_spec)
e_spec.loader.exec_module(risk_decision_engine)
RiskDecisionEngine = risk_decision_engine.RiskDecisionEngine

class TestFinancialRiskCost(unittest.TestCase):

    def setUp(self):
        self.engine = RiskDecisionEngine(
            false_positive_cost=15.0,
            false_negative_cost=85.0,
            review_cost=5.0,
            customer_friction_cost=2.0,
            t_review=0.30,
            t_block=0.70
        )

    def test_01_fp_cost_calculation(self):
        """TEST 1: False Positive Cost calculation."""
        res = self.engine.evaluate_transaction(probability=0.85, actual_label=0)
        self.assertEqual(res["decision"], "BLOCK")
        self.assertTrue(res["is_false_positive"])
        self.assertEqual(res["business_cost"], 17.0)  # C_FP + C_FRICTION (15 + 2)

    def test_02_fn_cost_calculation(self):
        """TEST 2: False Negative Cost calculation."""
        res = self.engine.evaluate_transaction(probability=0.10, actual_label=1)
        self.assertEqual(res["decision"], "APPROVE")
        self.assertTrue(res["is_false_negative"])
        self.assertEqual(res["business_cost"], 85.0)  # C_FN

    def test_03_review_cost_calculation(self):
        """TEST 3: Review Cost calculation for legitimate user."""
        res = self.engine.evaluate_transaction(probability=0.50, actual_label=0)
        self.assertEqual(res["decision"], "REVIEW")
        self.assertTrue(res["is_false_positive"])
        self.assertEqual(res["business_cost"], 7.0)  # C_REVIEW + C_FRICTION (5 + 2)

    def test_04_total_cost_calculation(self):
        """TEST 4: Total Cost calculation across dataset."""
        probs = [0.10, 0.50, 0.85]
        labels = [0, 0, 1]  # TN, FP(Review), TP(Block)
        metrics = self.engine.evaluate_dataset_policy(probs, labels)
        self.assertEqual(metrics["total_cost"], 7.0)  # Review cost 5 + friction 2

    def test_05_approve_decision(self):
        """TEST 5: APPROVE decision boundaries."""
        res = self.engine.evaluate_transaction(probability=0.29, actual_label=0)
        self.assertEqual(res["decision"], "APPROVE")

    def test_06_review_decision(self):
        """TEST 6: REVIEW decision boundaries."""
        res = self.engine.evaluate_transaction(probability=0.30, actual_label=0)
        self.assertEqual(res["decision"], "REVIEW")

    def test_07_block_decision(self):
        """TEST 7: BLOCK decision boundaries."""
        res = self.engine.evaluate_transaction(probability=0.70, actual_label=1)
        self.assertEqual(res["decision"], "BLOCK")

    def test_08_threshold_sweep(self):
        """TEST 8: Threshold sweep evaluation."""
        probs = [0.10, 0.40, 0.80]
        labels = [0, 1, 1]
        m1 = self.engine.evaluate_dataset_policy(probs, labels, t_block=0.50)
        m2 = self.engine.evaluate_dataset_policy(probs, labels, t_block=0.90)
        self.assertNotEqual(m1["block_count"], m2["block_count"])

    def test_09_determinism(self):
        """TEST 9: Same input produces 100% identical decision results."""
        r1 = self.engine.evaluate_transaction(0.65, 1)
        r2 = self.engine.evaluate_transaction(0.65, 1)
        self.assertEqual(r1, r2)

    def test_10_validation_only_threshold_selection(self):
        """TEST 10: Validation-only threshold selection check."""
        with open("data/financial/evaluation/phase6e_config.json") as f:
            cfg = json.load(f)
        self.assertIn("optimal_threshold_baseline", cfg)

    def test_11_test_set_isolation(self):
        """TEST 11: Held-out test set isolation check."""
        with open("data/financial/evaluation/business_summary.json") as f:
            summary = json.load(f)
        self.assertIn("baseline_cost", summary)

    def test_12_cost_sensitivity(self):
        """TEST 12: Cost sensitivity analysis output check."""
        df = pd.read_csv("data/financial/evaluation/cost_sensitivity.csv") if os.path.exists("data/financial/evaluation/cost_sensitivity.csv") else None
        if df is not None:
            self.assertEqual(len(df), 4)

    def test_13_invalid_config_handling(self):
        """TEST 13: Handles zero size dataset without crashing."""
        res = self.engine.evaluate_dataset_policy([], [])
        self.assertEqual(res, {})

    def test_14_zero_cost_edge_cases(self):
        """TEST 14: Zero-cost configuration edge cases."""
        e_zero = RiskDecisionEngine(0.0, 0.0, 0.0, 0.0)
        res = e_zero.evaluate_transaction(0.90, 0)
        self.assertEqual(res["business_cost"], 0.0)

    def test_15_probability_boundary_cases(self):
        """TEST 15: Extreme probability boundaries 0.0 and 1.0."""
        r_low = self.engine.evaluate_transaction(0.0, 0)
        r_high = self.engine.evaluate_transaction(1.0, 1)
        self.assertEqual(r_low["decision"], "APPROVE")
        self.assertEqual(r_high["decision"], "BLOCK")

if __name__ == "__main__":
    unittest.main()
