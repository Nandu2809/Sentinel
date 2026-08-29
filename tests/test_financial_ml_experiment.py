#!/usr/bin/env python3
"""
Sentinel Phase 6D — Unit Test Suite for Baseline vs Enhanced ML Risk Engine Experiment
"""

import os
import json
import unittest
import pandas as pd
from importlib.util import module_from_spec, spec_from_file_location

exp_path = os.path.abspath(os.path.join(os.path.dirname(__file__), "../ai-engine/anomaly-detection/app/financial/financial_ml_experiment.py"))
spec = spec_from_file_location("financial_ml_experiment", exp_path)
financial_ml_experiment = module_from_spec(spec)
spec.loader.exec_module(financial_ml_experiment)

class TestFinancialMLExperiment(unittest.TestCase):

    def setUp(self):
        self.eval_dir = "data/financial/evaluation"

    def test_01_evaluation_artifacts_exist(self):
        """TEST 1: Verify all required Phase 6D evaluation output files are generated."""
        required_files = [
            "baseline_model_metrics.json",
            "enhanced_model_metrics.json",
            "comparison.csv",
            "confusion_matrix_baseline.csv",
            "confusion_matrix_enhanced.csv",
            "feature_importance.csv",
            "ablation_results.csv",
            "experiment_config.json"
        ]
        for f in required_files:
            p = os.path.join(self.eval_dir, f)
            self.assertTrue(os.path.exists(p), f"Missing evaluation output file: {p}")

    def test_02_feature_leakage_prohibited_columns(self):
        """TEST 2: Verify isFraud, fraudScenario, transactionId are never used as predictive features."""
        df_comp = pd.read_csv(os.path.join(self.eval_dir, "comparison.csv"))
        self.assertNotIn("isFraud", df_comp.columns)
        self.assertNotIn("fraudScenario", df_comp.columns)

        df_fi = pd.read_csv(os.path.join(self.eval_dir, "feature_importance.csv"))
        fi_features = df_fi["feature_name"].tolist()
        self.assertNotIn("isFraud", fi_features)
        self.assertNotIn("fraudScenario", fi_features)
        self.assertNotIn("transactionId", fi_features)

    def test_03_train_val_test_split_sizes(self):
        """TEST 3: Verify train (7000), val (1000), test (2000) split sizes in experiment config."""
        with open(os.path.join(self.eval_dir, "experiment_config.json")) as f:
            config = json.load(f)

        self.assertEqual(config["train_size"], 7000)
        self.assertEqual(config["val_size"], 1000)
        self.assertEqual(config["test_size"], 2000)

    def test_04_feature_counts(self):
        """TEST 4: Verify baseline feature count (9) and enhanced feature count (34)."""
        with open(os.path.join(self.eval_dir, "experiment_config.json")) as f:
            config = json.load(f)

        self.assertEqual(config["baseline_feature_count"], 9)
        self.assertEqual(config["enhanced_feature_count"], 34)

    def test_05_metrics_validity(self):
        """TEST 5: Verify all metric values are within valid probability ranges [0, 1]."""
        with open(os.path.join(self.eval_dir, "baseline_model_metrics.json")) as f:
            base = json.load(f)
        with open(os.path.join(self.eval_dir, "enhanced_model_metrics.json")) as f:
            enh = json.load(f)

        for m_name in ["logistic_regression", "random_forest"]:
            b_m = base[m_name]
            e_m = enh[m_name]

            for score_key in ["precision", "recall", "f1", "roc_auc", "pr_auc"]:
                self.assertGreaterEqual(b_m[score_key], 0.0)
                self.assertLessEqual(b_m[score_key], 1.0)
                self.assertGreaterEqual(e_m[score_key], 0.0)
                self.assertLessEqual(e_m[score_key], 1.0)

    def test_06_ablation_results_completeness(self):
        """TEST 6: Verify ablation results cover all 5 required feature subsets."""
        df_abl = pd.read_csv(os.path.join(self.eval_dir, "ablation_results.csv"))
        self.assertEqual(len(df_abl), 5)
        configs = df_abl["configuration"].tolist()
        self.assertIn("Baseline", configs)
        self.assertIn("Baseline + Device Graph", configs)
        self.assertIn("Baseline + IP Graph", configs)
        self.assertIn("Baseline + Payment Graph", configs)
        self.assertIn("Baseline + All Graph (Enhanced)", configs)

if __name__ == "__main__":
    unittest.main()
