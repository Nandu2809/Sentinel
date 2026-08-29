#!/usr/bin/env python3
"""
Python Unit & Integration Test Suite for Synthetic Financial Risk Dataset Generator
Phase 6B Verification.
"""

import sys
import os
import shutil
import tempfile
import unittest
import pandas as pd

# Add repository root to sys.path
sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), "../../../")))
from scripts.financial_dataset_generator import generate_dataset

class TestFinancialDatasetGenerator(unittest.TestCase):

    def setUp(self):
        self.test_dir_1 = tempfile.mkdtemp()
        self.test_dir_2 = tempfile.mkdtemp()

    def tearDown(self):
        shutil.rmtree(self.test_dir_1, ignore_errors=True)
        shutil.rmtree(self.test_dir_2, ignore_errors=True)

    def test_seed_determinism(self):
        """Verify that identical seeds produce 100% byte-for-byte identical datasets."""
        generate_dataset(seed=42, size=500, output_dir=self.test_dir_1)
        generate_dataset(seed=42, size=500, output_dir=self.test_dir_2)

        df1 = pd.read_csv(os.path.join(self.test_dir_1, "financial_events.csv"))
        df2 = pd.read_csv(os.path.join(self.test_dir_2, "financial_events.csv"))

        pd.testing.assert_frame_equal(df1, df2)

    def test_no_split_overlap(self):
        """Verify zero transactionId overlap between Train, Validation, and Test datasets."""
        generate_dataset(seed=42, size=500, output_dir=self.test_dir_1)
        df_train = pd.read_csv(os.path.join(self.test_dir_1, "train.csv"))
        df_val = pd.read_csv(os.path.join(self.test_dir_1, "validation.csv"))
        df_test = pd.read_csv(os.path.join(self.test_dir_1, "test.csv"))

        train_ids = set(df_train["transactionId"])
        val_ids = set(df_val["transactionId"])
        test_ids = set(df_test["transactionId"])

        self.assertEqual(len(train_ids.intersection(val_ids)), 0)
        self.assertEqual(len(train_ids.intersection(test_ids)), 0)

if __name__ == "__main__":
    unittest.main()
