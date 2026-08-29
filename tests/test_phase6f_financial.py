"""
Sentinel Phase 6F — Comprehensive Unit Tests for Hardened Financial Risk System
"""

import os
import sys
import unittest
import tempfile
import pandas as pd
import json
from importlib.util import module_from_spec, spec_from_file_location

# Load hardened generator
gen_path = os.path.abspath(os.path.join(os.path.dirname(__file__), "../scripts/hardened_financial_dataset_generator.py"))
g_spec = spec_from_file_location("hardened_financial_dataset_generator", gen_path)
hardened_generator = module_from_spec(g_spec)
g_spec.loader.exec_module(hardened_generator)

# Load hardened feature pipeline
pipe_path = os.path.abspath(os.path.join(os.path.dirname(__file__), "../ai-engine/anomaly-detection/app/financial/hardened_feature_pipeline.py"))
p_spec = spec_from_file_location("hardened_feature_pipeline", pipe_path)
hardened_pipeline = module_from_spec(p_spec)
p_spec.loader.exec_module(hardened_pipeline)

# Load risk decision engine
eng_path = os.path.abspath(os.path.join(os.path.dirname(__file__), "../ai-engine/anomaly-detection/app/financial/risk_decision_engine.py"))
e_spec = spec_from_file_location("risk_decision_engine", eng_path)
risk_decision_engine = module_from_spec(e_spec)
e_spec.loader.exec_module(risk_decision_engine)
RiskDecisionEngine = risk_decision_engine.RiskDecisionEngine

class TestPhase6FFinancial(unittest.TestCase):

    def setUp(self):
        self.tmp_dir = tempfile.TemporaryDirectory()

    def tearDown(self):
        self.tmp_dir.cleanup()

    def test_hardened_dataset_generator(self):
        output_dir = self.tmp_dir.name
        hardened_generator.generate_hardened_dataset(
            seed=42, size=1000, fraud_rate=0.08, output_dir=output_dir,
            train_ratio=0.70, val_ratio=0.10, test_ratio=0.20
        )

        self.assertTrue(os.path.exists(os.path.join(output_dir, "financial_events_hardened.csv")))
        self.assertTrue(os.path.exists(os.path.join(output_dir, "train.csv")))
        self.assertTrue(os.path.exists(os.path.join(output_dir, "validation.csv")))
        self.assertTrue(os.path.exists(os.path.join(output_dir, "test.csv")))

        df = pd.read_csv(os.path.join(output_dir, "financial_events_hardened.csv"))
        self.assertEqual(len(df), 1000)

        # Verify hardened scenarios present
        scenarios = df["fraudScenario"].unique()
        self.assertIn("EVASIVE_FRAUD", scenarios)
        self.assertIn("LEGITIMATE_SHARED_INFRASTRUCTURE", scenarios)
        self.assertIn("LOW_AND_SLOW_RING", scenarios)

    def test_no_target_or_future_leakage(self):
        output_dir = self.tmp_dir.name
        hardened_generator.generate_hardened_dataset(seed=42, size=500, output_dir=output_dir)

        df = pd.read_csv(os.path.join(output_dir, "financial_events_hardened.csv"))
        manifest = hardened_pipeline.get_hardened_feature_manifest()

        predictive_feats = [f["feature_name"] for f in manifest["baseline_features"] + manifest["enhanced_features"]]
        self.assertNotIn("isFraud", predictive_feats)
        self.assertNotIn("fraudScenario", predictive_feats)

        for feat in manifest["baseline_features"] + manifest["enhanced_features"]:
            self.assertTrue(feat["available_at_decision_time"])
            self.assertFalse(feat["uses_future_data"])

    def test_risk_decision_engine_3tier(self):
        engine = RiskDecisionEngine(false_positive_cost=15.0, false_negative_cost=85.0, review_cost=5.0, customer_friction_cost=2.0, t_review=0.20, t_block=0.50)

        res_approve = engine.evaluate_transaction(0.05, 0)
        self.assertEqual(res_approve["decision"], "APPROVE")
        self.assertEqual(res_approve["business_cost"], 0.0)

        res_review = engine.evaluate_transaction(0.35, 0)
        self.assertEqual(res_review["decision"], "REVIEW")
        self.assertEqual(res_review["business_cost"], 7.0) # 5.0 review + 2.0 friction

        res_block = engine.evaluate_transaction(0.85, 1)
        self.assertEqual(res_block["decision"], "BLOCK")

if __name__ == "__main__":
    unittest.main()
