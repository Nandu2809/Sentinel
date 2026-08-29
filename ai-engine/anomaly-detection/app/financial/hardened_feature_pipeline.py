"""
Sentinel Phase 6F — Hardened Financial Feature Pipeline Coordinator

Executes feature extraction on Phase 6F hardened datasets,
generating baseline and enhanced feature outputs with zero target leakage.
"""

import os
import sys
import json
import time
import pandas as pd
from datetime import datetime, timezone
from importlib.util import module_from_spec, spec_from_file_location

graph_path = os.path.abspath(os.path.join(os.path.dirname(__file__), "financial_graph.py"))
g_spec = spec_from_file_location("financial_graph", graph_path)
financial_graph = module_from_spec(g_spec)
g_spec.loader.exec_module(financial_graph)
FinancialEntityGraph = financial_graph.FinancialEntityGraph

feat_path = os.path.abspath(os.path.join(os.path.dirname(__file__), "financial_features.py"))
f_spec = spec_from_file_location("financial_features", feat_path)
financial_features = module_from_spec(f_spec)
f_spec.loader.exec_module(financial_features)
FinancialFeatureExtractor = financial_features.FinancialFeatureExtractor

PHASE6F_DIR = "data/financial/phase6f"
BASELINE_OUTPUT_PATH = os.path.join(PHASE6F_DIR, "features_baseline.csv")
ENHANCED_OUTPUT_PATH = os.path.join(PHASE6F_DIR, "features_enhanced.csv")
FEATURE_MANIFEST_PATH = os.path.join(PHASE6F_DIR, "feature_manifest.json")

def get_hardened_feature_manifest():
    manifest = {
        "manifest_version": "2.0.0-Phase6F",
        "generated_at": datetime.now(timezone.utc).isoformat(),
        "baseline_features": [
            {"feature_name": "amount", "category": "transaction", "type": "float", "available_at_decision_time": True, "uses_future_data": False},
            {"feature_name": "logAmount", "category": "transaction", "type": "float", "available_at_decision_time": True, "uses_future_data": False},
            {"feature_name": "accountAgeDays", "category": "account", "type": "int", "available_at_decision_time": True, "uses_future_data": False},
            {"feature_name": "velocity1h", "category": "velocity", "type": "int", "available_at_decision_time": True, "uses_future_data": False},
            {"feature_name": "failedTxCount24h", "category": "velocity", "type": "int", "available_at_decision_time": True, "uses_future_data": False},
            {"feature_name": "sharedDeviceAccountCount", "category": "device", "type": "int", "available_at_decision_time": True, "uses_future_data": False},
            {"feature_name": "sharedIpAccountCount", "category": "ip", "type": "int", "available_at_decision_time": True, "uses_future_data": False},
            {"feature_name": "isNewAccountFlag", "category": "account", "type": "int", "available_at_decision_time": True, "uses_future_data": False},
            {"feature_name": "highVelocityFlag", "category": "velocity", "type": "int", "available_at_decision_time": True, "uses_future_data": False}
        ],
        "enhanced_features": [
            {"feature_name": "sharedPaymentAccountCount", "category": "payment", "type": "int", "available_at_decision_time": True, "uses_future_data": False},
            {"feature_name": "deviceDegree", "category": "relationship", "type": "int", "available_at_decision_time": True, "uses_future_data": False},
            {"feature_name": "ipDegree", "category": "relationship", "type": "int", "available_at_decision_time": True, "uses_future_data": False},
            {"feature_name": "paymentMethodDegree", "category": "relationship", "type": "int", "available_at_decision_time": True, "uses_future_data": False},
            {"feature_name": "merchantDegree", "category": "relationship", "type": "int", "available_at_decision_time": True, "uses_future_data": False},
            {"feature_name": "uniqueDevicesPerUser", "category": "relationship", "type": "int", "available_at_decision_time": True, "uses_future_data": False},
            {"feature_name": "uniqueIpsPerUser", "category": "relationship", "type": "int", "available_at_decision_time": True, "uses_future_data": False},
            {"feature_name": "uniquePaymentMethodsPerUser", "category": "relationship", "type": "int", "available_at_decision_time": True, "uses_future_data": False},
            {"feature_name": "recentSharedDeviceCount", "category": "relationship", "type": "int", "available_at_decision_time": True, "uses_future_data": False},
            {"feature_name": "recentSharedIpCount", "category": "relationship", "type": "int", "available_at_decision_time": True, "uses_future_data": False},
            {"feature_name": "recentPaymentReuseCount", "category": "relationship", "type": "int", "available_at_decision_time": True, "uses_future_data": False},
            {"feature_name": "transactionVelocityOnSharedDevice", "category": "relationship", "type": "int", "available_at_decision_time": True, "uses_future_data": False},
            {"feature_name": "transactionVelocityOnSharedIp", "category": "relationship", "type": "int", "available_at_decision_time": True, "uses_future_data": False},
            {"feature_name": "deviceClusterSize", "category": "cluster", "type": "int", "available_at_decision_time": True, "uses_future_data": False},
            {"feature_name": "ipClusterSize", "category": "cluster", "type": "int", "available_at_decision_time": True, "uses_future_data": False},
            {"feature_name": "paymentClusterSize", "category": "cluster", "type": "int", "available_at_decision_time": True, "uses_future_data": False},
            {"feature_name": "multiAccountDeviceFlag", "category": "cluster", "type": "int", "available_at_decision_time": True, "uses_future_data": False},
            {"feature_name": "multiAccountIpFlag", "category": "cluster", "type": "int", "available_at_decision_time": True, "uses_future_data": False},
            {"feature_name": "multiAccountPaymentFlag", "category": "cluster", "type": "int", "available_at_decision_time": True, "uses_future_data": False},
            {"feature_name": "sharedInfrastructureScore", "category": "cluster", "type": "float", "available_at_decision_time": True, "uses_future_data": False},
            {"feature_name": "relationshipDensity", "category": "cluster", "type": "float", "available_at_decision_time": True, "uses_future_data": False},
            {"feature_name": "ringConnectivityScore", "category": "cluster", "type": "float", "available_at_decision_time": True, "uses_future_data": False}
        ]
    }
    return manifest

def run_hardened_pipeline(input_csv=os.path.join(PHASE6F_DIR, "financial_events_hardened.csv"),
                          baseline_out=BASELINE_OUTPUT_PATH,
                          enhanced_out=ENHANCED_OUTPUT_PATH,
                          manifest_out=FEATURE_MANIFEST_PATH):
    print(f"Loading Phase 6F hardened dataset from {input_csv}...")
    df = pd.read_csv(input_csv)

    df["timestamp"] = pd.to_datetime(df["timestamp"])
    df = df.sort_values("timestamp").reset_index(drop=True)

    records = df.to_dict("records")
    graph = FinancialEntityGraph()
    extractor = FinancialFeatureExtractor()

    base_rows = []
    enh_rows = []

    t0 = time.time()
    for record in records:
        record["timestamp"] = str(record["timestamp"])

        base_f = extractor.extract_baseline_features(record)
        enh_f = extractor.extract_enhanced_features(record, graph)

        graph.add_transaction(record)

        base_rows.append({"transactionId": record["transactionId"], "isFraud": record["isFraud"], **base_f})
        enh_rows.append({"transactionId": record["transactionId"], "isFraud": record["isFraud"], **enh_f})

    elapsed = time.time() - t0

    df_base = pd.DataFrame(base_rows)
    df_enh = pd.DataFrame(enh_rows)

    df_base.to_csv(baseline_out, index=False)
    df_enh.to_csv(enhanced_out, index=False)

    manifest = get_hardened_feature_manifest()
    with open(manifest_out, "w", encoding="utf-8") as f:
        json.dump(manifest, f, indent=2)

    print(f"\n=== Phase 6F Hardened Feature Pipeline Complete ===")
    print(f"Processed Records : {len(records)}")
    print(f"Execution Time    : {elapsed:.3f} seconds ({len(records)/elapsed:.1f} rec/sec)")
    print(f"Baseline Output   : {baseline_out}")
    print(f"Enhanced Output   : {enhanced_out}")
    print(f"Manifest Output   : {manifest_out}")

    return elapsed

if __name__ == "__main__":
    run_hardened_pipeline()
