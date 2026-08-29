#!/usr/bin/env python3
"""
Sentinel Phase 6C — Comprehensive Test Suite for Financial Feature & Relationship Graph Engine
Verifies graph topology, temporal correctness, anti-leakage, baseline/enhanced splits, and numerical stability.
"""

import unittest
import math
import os
import tempfile
import pandas as pd
from datetime import datetime, timezone
from importlib.util import module_from_spec, spec_from_file_location

# Dynamically load Phase 6C modules
graph_path = os.path.abspath(os.path.join(os.path.dirname(__file__), "../ai-engine/anomaly-detection/app/financial/financial_graph.py"))
g_spec = spec_from_file_location("financial_graph", graph_path)
financial_graph = module_from_spec(g_spec)
g_spec.loader.exec_module(financial_graph)
FinancialEntityGraph = financial_graph.FinancialEntityGraph

feat_path = os.path.abspath(os.path.join(os.path.dirname(__file__), "../ai-engine/anomaly-detection/app/financial/financial_features.py"))
f_spec = spec_from_file_location("financial_features", feat_path)
financial_features = module_from_spec(f_spec)
f_spec.loader.exec_module(financial_features)
FinancialFeatureExtractor = financial_features.FinancialFeatureExtractor

pipe_path = os.path.abspath(os.path.join(os.path.dirname(__file__), "../ai-engine/anomaly-detection/app/financial/feature_pipeline.py"))
p_spec = spec_from_file_location("feature_pipeline", pipe_path)
feature_pipeline = module_from_spec(p_spec)
p_spec.loader.exec_module(feature_pipeline)

class TestFinancialFeatureGraph(unittest.TestCase):

    def setUp(self):
        self.graph = FinancialEntityGraph()
        self.extractor = FinancialFeatureExtractor()

        self.tx1 = {
            "transactionId": "tx_001",
            "userId": "usr_101",
            "merchantId": "merch_001",
            "amount": 1500.0,
            "currency": "INR",
            "timestamp": "2026-08-01T10:00:00+00:00",
            "deviceId": "dev_999",
            "ipAddress": "192.168.1.1",
            "location": "Mumbai, IN",
            "paymentMethodRef": "pm_tok_111",
            "accountAgeDays": 100,
            "velocity1h": 1,
            "failedTxCount24h": 0,
            "sharedDeviceAccountCount": 1,
            "sharedIpAccountCount": 1,
            "isFraud": 0,
            "fraudScenario": "NONE"
        }

        self.tx2 = {
            "transactionId": "tx_002",
            "userId": "usr_102",
            "merchantId": "merch_001",
            "amount": 2500.0,
            "currency": "INR",
            "timestamp": "2026-08-01T10:15:00+00:00",
            "deviceId": "dev_999",  # Shared device
            "ipAddress": "192.168.1.1",  # Shared IP
            "location": "Mumbai, IN",
            "paymentMethodRef": "pm_tok_111",  # Shared payment
            "accountAgeDays": 5,
            "velocity1h": 3,
            "failedTxCount24h": 1,
            "sharedDeviceAccountCount": 2,
            "sharedIpAccountCount": 2,
            "isFraud": 1,
            "fraudScenario": "COMBINED_RING"
        }

        self.tx3 = {
            "transactionId": "tx_003",
            "userId": "usr_103",
            "merchantId": "merch_002",
            "amount": 5000.0,
            "currency": "INR",
            "timestamp": "2026-08-01T10:30:00+00:00",
            "deviceId": "dev_999",  # Shared device
            "ipAddress": "10.0.0.1",  # Different IP
            "location": "Delhi, IN",
            "paymentMethodRef": "pm_tok_333",
            "accountAgeDays": 2,
            "velocity1h": 5,
            "failedTxCount24h": 0,
            "sharedDeviceAccountCount": 3,
            "sharedIpAccountCount": 1,
            "isFraud": 1,
            "fraudScenario": "DEVICE_SHARING_RING"
        }

    def test_01_graph_construction(self):
        """TEST 1: Graph construction from known synthetic transactions."""
        self.graph.add_transaction(self.tx1)
        self.assertIn("usr_101", self.graph.users)
        self.assertIn("dev_999", self.graph.devices)
        self.assertIn("192.168.1.1", self.graph.ips)

    def test_02_user_device_relationship(self):
        """TEST 2: User-device relationship is correctly created."""
        self.graph.add_transaction(self.tx1)
        metrics = self.graph.get_relationship_metrics(self.tx1)
        self.assertGreaterEqual(metrics["deviceDegree"], 2)
        self.assertEqual(metrics["uniqueDevicesPerUser"], 1)

    def test_03_user_ip_relationship(self):
        """TEST 3: User-IP relationship is correctly created."""
        self.graph.add_transaction(self.tx1)
        metrics = self.graph.get_relationship_metrics(self.tx1)
        self.assertGreaterEqual(metrics["ipDegree"], 2)
        self.assertEqual(metrics["uniqueIpsPerUser"], 1)

    def test_04_payment_reference_reuse_detected(self):
        """TEST 4: Payment reference reuse is detected across multiple accounts."""
        self.graph.add_transaction(self.tx1)
        m1 = self.graph.get_relationship_metrics(self.tx1)
        self.assertEqual(m1["sharedPaymentAccountCount"], 1)

        self.graph.add_transaction(self.tx2)
        m2 = self.graph.get_relationship_metrics(self.tx2)
        self.assertEqual(m2["sharedPaymentAccountCount"], 2)

    def test_05_multiple_accounts_sharing_device(self):
        """TEST 5: Multiple accounts sharing one device are detected."""
        self.graph.add_transaction(self.tx1)
        self.graph.add_transaction(self.tx2)
        self.graph.add_transaction(self.tx3)

        m3 = self.graph.get_relationship_metrics(self.tx3)
        self.assertEqual(m3["sharedDeviceAccountCount"], 3)
        self.assertEqual(m3["multiAccountDeviceFlag"], 1)

    def test_06_multiple_accounts_sharing_ip(self):
        """TEST 6: Multiple accounts sharing one IP are detected."""
        self.graph.add_transaction(self.tx1)
        self.graph.add_transaction(self.tx2)

        m2 = self.graph.get_relationship_metrics(self.tx2)
        self.assertEqual(m2["sharedIpAccountCount"], 2)
        self.assertEqual(m2["multiAccountIpFlag"], 1)

    def test_07_temporal_leakage_prevention(self):
        """TEST 7: Temporal leakage prevention (future transactions do NOT affect past features)."""
        # Calculate metrics for tx1 BEFORE tx2 occurs
        metrics_tx1_before_tx2 = self.graph.get_relationship_metrics(self.tx1)
        self.assertEqual(metrics_tx1_before_tx2["sharedDeviceAccountCount"], 1)

        # Add tx2 into graph (15 minutes later)
        self.graph.add_transaction(self.tx1)
        self.graph.add_transaction(self.tx2)

        # Query tx1 features strictly at tx1 timestamp
        metrics_tx1_historical = self.graph.get_relationship_metrics(self.tx1)
        self.assertEqual(metrics_tx1_historical["sharedDeviceAccountCount"], 1)

    def test_08_no_fraud_label_leakage(self):
        """TEST 8: No fraud label leakage (isFraud and fraudScenario excluded from predictive feature vectors)."""
        base_feats = self.extractor.extract_baseline_features(self.tx2)
        enh_feats = self.extractor.extract_enhanced_features(self.tx2, self.graph)

        self.assertNotIn("isFraud", base_feats)
        self.assertNotIn("fraudScenario", base_feats)
        self.assertNotIn("isFraud", enh_feats)
        self.assertNotIn("fraudScenario", enh_feats)

    def test_09_deterministic_output(self):
        """TEST 9: Same input produces 100% identical feature values."""
        g1 = FinancialEntityGraph()
        e1 = FinancialFeatureExtractor()
        g1.add_transaction(self.tx1)
        f1 = e1.extract_enhanced_features(self.tx1, g1)

        g2 = FinancialEntityGraph()
        e2 = FinancialFeatureExtractor()
        g2.add_transaction(self.tx1)
        f2 = e2.extract_enhanced_features(self.tx1, g2)

        self.assertEqual(f1, f2)

    def test_10_baseline_feature_set_contains_no_graph_features(self):
        """TEST 10: Baseline feature set contains NO graph features."""
        base_feats = self.extractor.extract_baseline_features(self.tx1)
        graph_only_keys = [
            "deviceDegree", "ipDegree", "paymentMethodDegree", "merchantDegree",
            "deviceClusterSize", "ipClusterSize", "ringConnectivityScore"
        ]
        for key in graph_only_keys:
            self.assertNotIn(key, base_feats)

    def test_11_enhanced_feature_set_contains_baseline_plus_graph(self):
        """TEST 11: Enhanced feature set contains baseline + graph features."""
        self.graph.add_transaction(self.tx1)
        base_feats = self.extractor.extract_baseline_features(self.tx1)
        enh_feats = self.extractor.extract_enhanced_features(self.tx1, self.graph)

        for b_key in base_feats:
            self.assertIn(b_key, enh_feats)

        self.assertIn("deviceDegree", enh_feats)
        self.assertIn("deviceClusterSize", enh_feats)
        self.assertIn("ringConnectivityScore", enh_feats)

    def test_12_no_nan_or_infinite_values(self):
        """TEST 12: No NaN / infinite values in generated numerical features."""
        self.graph.add_transaction(self.tx1)
        self.graph.add_transaction(self.tx2)
        enh_feats = self.extractor.extract_enhanced_features(self.tx2, self.graph)

        for k, v in enh_feats.items():
            if isinstance(v, float):
                self.assertFalse(math.isnan(v), f"Feature {k} contains NaN")
                self.assertFalse(math.isinf(v), f"Feature {k} contains Inf")

if __name__ == "__main__":
    unittest.main()
