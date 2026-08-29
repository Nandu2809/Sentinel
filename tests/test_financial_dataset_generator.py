#!/usr/bin/env python3
"""
Python Unit & Integration Test Suite for Synthetic Financial Risk Dataset Generator
Phase 6B Verification.
"""

import os
import shutil
import tempfile
import unittest
import pandas as pd
from scripts.financial_dataset_generator import generate_dataset

class TestFinancialDatasetGenerator(unittest.TestCase):

    def setUp(self):
        self.test_dir_1 = tempfile.mkdtemp()
        self.test_dir_2 = tempfile.mkdtemp()
        self.test_dir_3 = tempfile.mkdtemp()

    def tearDown(self):
        shutil.rmtree(self.test_dir_1, ignore_errors=True)
        shutil.rmtree(self.test_dir_2, ignore_errors=True)
        shutil.rmtree(self.test_dir_3, ignore_errors=True)

    def test_seed_determinism(self):
        """Verify that identical seeds produce 100% byte-for-byte identical datasets."""
        generate_dataset(seed=42, size=1000, output_dir=self.test_dir_1)
        generate_dataset(seed=42, size=1000, output_dir=self.test_dir_2)

        df1 = pd.read_csv(os.path.join(self.test_dir_1, "financial_events.csv"))
        df2 = pd.read_csv(os.path.join(self.test_dir_2, "financial_events.csv"))

        pd.testing.assert_frame_equal(df1, df2)

    def test_different_seeds_produce_different_data(self):
        """Verify that different seeds produce distinct dataset outputs."""
        generate_dataset(seed=42, size=1000, output_dir=self.test_dir_1)
        generate_dataset(seed=99, size=1000, output_dir=self.test_dir_3)

        df1 = pd.read_csv(os.path.join(self.test_dir_1, "financial_events.csv"))
        df3 = pd.read_csv(os.path.join(self.test_dir_3, "financial_events.csv"))

        self.assertNotEqual(df1["transactionId"].tolist(), df3["transactionId"].tolist())

    def test_record_counts_and_ratios(self):
        """Verify record counts, splits, and target fraud prevalence."""
        generate_dataset(seed=123, size=2000, fraud_rate=0.10, train_ratio=0.70, val_ratio=0.10, test_ratio=0.20, output_dir=self.test_dir_1)

        df_full = pd.read_csv(os.path.join(self.test_dir_1, "financial_events.csv"))
        df_train = pd.read_csv(os.path.join(self.test_dir_1, "train.csv"))
        df_val = pd.read_csv(os.path.join(self.test_dir_1, "validation.csv"))
        df_test = pd.read_csv(os.path.join(self.test_dir_1, "test.csv"))

        self.assertEqual(len(df_full), 2000)
        self.assertEqual(len(df_train), 1400)
        self.assertEqual(len(df_val), 200)
        self.assertEqual(len(df_test), 400)

        fraud_count = df_full["isFraud"].sum()
        self.assertEqual(fraud_count, 200)  # 10% of 2000

    def test_fraud_scenarios_presence(self):
        """Verify that all 5 coordinated abuse fraud scenarios are present in dataset."""
        generate_dataset(seed=42, size=1000, output_dir=self.test_dir_1)
        df_full = pd.read_csv(os.path.join(self.test_dir_1, "financial_events.csv"))

        expected_scenarios = {
            "NONE",
            "DEVICE_SHARING_RING",
            "SHARED_IP_BURST",
            "PAYMENT_REF_REUSE",
            "BEHAVIORAL_ANOMALY",
            "COMBINED_RING"
        }
        actual_scenarios = set(df_full["fraudScenario"].unique())
        self.assertEqual(expected_scenarios, actual_scenarios)

    def test_no_split_overlap(self):
        """Verify zero transactionId overlap between Train, Validation, and Test datasets."""
        generate_dataset(seed=42, size=1000, output_dir=self.test_dir_1)
        df_train = pd.read_csv(os.path.join(self.test_dir_1, "train.csv"))
        df_val = pd.read_csv(os.path.join(self.test_dir_1, "validation.csv"))
        df_test = pd.read_csv(os.path.join(self.test_dir_1, "test.csv"))

        train_ids = set(df_train["transactionId"])
        val_ids = set(df_val["transactionId"])
        test_ids = set(df_test["transactionId"])

        self.assertEqual(len(train_ids.intersection(val_ids)), 0)
        self.assertEqual(len(train_ids.intersection(test_ids)), 0)
        self.assertEqual(len(val_ids.intersection(test_ids)), 0)

    def test_data_leakage_safety(self):
        """Verify that predictive input feature columns contain no sensitive credentials or target labels."""
        generate_dataset(seed=42, size=100, output_dir=self.test_dir_1)
        df = pd.read_csv(os.path.join(self.test_dir_1, "financial_events.csv"))

        columns = list(df.columns)
        # Verify prohibited sensitive payment credentials
        for c in columns:
            self.assertNotIn("card", c.lower())
            self.assertNotIn("cvv", c.lower())
            self.assertNotIn("otp", c.lower())
            self.assertNotIn("password", c.lower())

        predictive_features = [c for c in columns if c not in ("isFraud", "fraudScenario")]
        self.assertNotIn("isFraud", predictive_features)
        self.assertNotIn("fraudScenario", predictive_features)

if __name__ == "__main__":
    unittest.main()
