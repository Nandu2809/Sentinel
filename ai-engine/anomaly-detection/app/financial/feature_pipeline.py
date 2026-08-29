"""
Sentinel Phase 6C — Financial Feature Pipeline Coordinator

Executes feature extraction on synthetic financial transaction datasets,
generating baseline and graph-enhanced feature datasets along with a machine-readable feature manifest.
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

FEATURE_MANIFEST_PATH = "data/financial/feature_manifest.json"
BASELINE_OUTPUT_PATH = "data/financial/features_baseline.csv"
ENHANCED_OUTPUT_PATH = "data/financial/features_enhanced.csv"

def get_feature_manifest():
    """Generates machine-readable feature manifest categorizing all baseline and enhanced features."""
    manifest = {
        "manifest_version": "1.0.0",
        "generated_at": datetime.now(timezone.utc).isoformat(),
        "baseline_features": [
            {
                "feature_name": "amount",
                "category": "transaction",
                "type": "float",
                "description": "Transaction amount in INR",
                "available_at_decision_time": True,
                "uses_future_data": False
            },
            {
                "feature_name": "logAmount",
                "category": "transaction",
                "type": "float",
                "description": "Log1p transformed transaction amount",
                "available_at_decision_time": True,
                "uses_future_data": False
            },
            {
                "feature_name": "accountAgeDays",
                "category": "account",
                "type": "int",
                "description": "Account age in days at transaction time",
                "available_at_decision_time": True,
                "uses_future_data": False
            },
            {
                "feature_name": "velocity1h",
                "category": "velocity",
                "type": "int",
                "description": "Transactions attempted by user in prior 1 hour",
                "available_at_decision_time": True,
                "uses_future_data": False
            },
            {
                "feature_name": "failedTxCount24h",
                "category": "velocity",
                "type": "int",
                "description": "Failed transaction attempts in prior 24 hours",
                "available_at_decision_time": True,
                "uses_future_data": False
            },
            {
                "feature_name": "sharedDeviceAccountCount",
                "category": "device",
                "type": "int",
                "description": "Accounts linked to device in transaction event",
                "available_at_decision_time": True,
                "uses_future_data": False
            },
            {
                "feature_name": "sharedIpAccountCount",
                "category": "ip",
                "type": "int",
                "description": "Accounts linked to IP in transaction event",
                "available_at_decision_time": True,
                "uses_future_data": False
            },
            {
                "feature_name": "isNewAccountFlag",
                "category": "account",
                "type": "int",
                "description": "Flag indicating account age <= 7 days",
                "available_at_decision_time": True,
                "uses_future_data": False
            },
            {
                "feature_name": "highVelocityFlag",
                "category": "velocity",
                "type": "int",
                "description": "Flag indicating velocity1h >= 5",
                "available_at_decision_time": True,
                "uses_future_data": False
            }
        ],
        "enhanced_features": [
            # Includes all baseline features plus relationship/graph features
            {
                "feature_name": "sharedPaymentAccountCount",
                "category": "payment",
                "type": "int",
                "description": "Accounts linked to payment method up to current event",
                "available_at_decision_time": True,
                "uses_future_data": False
            },
            {
                "feature_name": "deviceDegree",
                "category": "relationship",
                "type": "int",
                "description": "Degree of device node in entity graph",
                "available_at_decision_time": True,
                "uses_future_data": False
            },
            {
                "feature_name": "ipDegree",
                "category": "relationship",
                "type": "int",
                "description": "Degree of IP node in entity graph",
                "available_at_decision_time": True,
                "uses_future_data": False
            },
            {
                "feature_name": "paymentMethodDegree",
                "category": "relationship",
                "type": "int",
                "description": "Degree of payment method node in entity graph",
                "available_at_decision_time": True,
                "uses_future_data": False
            },
            {
                "feature_name": "merchantDegree",
                "category": "relationship",
                "type": "int",
                "description": "Degree of merchant node in entity graph",
                "available_at_decision_time": True,
                "uses_future_data": False
            },
            {
                "feature_name": "uniqueDevicesPerUser",
                "category": "relationship",
                "type": "int",
                "description": "Distinct devices linked to user",
                "available_at_decision_time": True,
                "uses_future_data": False
            },
            {
                "feature_name": "uniqueIpsPerUser",
                "category": "relationship",
                "type": "int",
                "description": "Distinct IPs linked to user",
                "available_at_decision_time": True,
                "uses_future_data": False
            },
            {
                "feature_name": "uniquePaymentMethodsPerUser",
                "category": "relationship",
                "type": "int",
                "description": "Distinct payment methods linked to user",
                "available_at_decision_time": True,
                "uses_future_data": False
            },
            {
                "feature_name": "recentSharedDeviceCount",
                "category": "relationship",
                "type": "int",
                "description": "Accounts sharing device in 1-hour window",
                "available_at_decision_time": True,
                "uses_future_data": False
            },
            {
                "feature_name": "recentSharedIpCount",
                "category": "relationship",
                "type": "int",
                "description": "Accounts sharing IP in 1-hour window",
                "available_at_decision_time": True,
                "uses_future_data": False
            },
            {
                "feature_name": "recentPaymentReuseCount",
                "category": "relationship",
                "type": "int",
                "description": "Accounts sharing payment method in 24-hour window",
                "available_at_decision_time": True,
                "uses_future_data": False
            },
            {
                "feature_name": "transactionVelocityOnSharedDevice",
                "category": "relationship",
                "type": "int",
                "description": "Transaction velocity across all accounts on device in 1h window",
                "available_at_decision_time": True,
                "uses_future_data": False
            },
            {
                "feature_name": "transactionVelocityOnSharedIp",
                "category": "relationship",
                "type": "int",
                "description": "Transaction velocity across all accounts on IP in 1h window",
                "available_at_decision_time": True,
                "uses_future_data": False
            },
            {
                "feature_name": "deviceClusterSize",
                "category": "cluster",
                "type": "int",
                "description": "2-hop entity cluster size surrounding device node",
                "available_at_decision_time": True,
                "uses_future_data": False
            },
            {
                "feature_name": "ipClusterSize",
                "category": "cluster",
                "type": "int",
                "description": "2-hop entity cluster size surrounding IP node",
                "available_at_decision_time": True,
                "uses_future_data": False
            },
            {
                "feature_name": "paymentClusterSize",
                "category": "cluster",
                "type": "int",
                "description": "2-hop entity cluster size surrounding payment reference node",
                "available_at_decision_time": True,
                "uses_future_data": False
            },
            {
                "feature_name": "multiAccountDeviceFlag",
                "category": "cluster",
                "type": "int",
                "description": "Flag indicating >1 account on device",
                "available_at_decision_time": True,
                "uses_future_data": False
            },
            {
                "feature_name": "multiAccountIpFlag",
                "category": "cluster",
                "type": "int",
                "description": "Flag indicating >1 account on IP",
                "available_at_decision_time": True,
                "uses_future_data": False
            },
            {
                "feature_name": "multiAccountPaymentFlag",
                "category": "cluster",
                "type": "int",
                "description": "Flag indicating >1 account on payment reference",
                "available_at_decision_time": True,
                "uses_future_data": False
            },
            {
                "feature_name": "sharedInfrastructureScore",
                "category": "cluster",
                "type": "float",
                "description": "Composite normalized shared infrastructure abuse score",
                "available_at_decision_time": True,
                "uses_future_data": False
            },
            {
                "feature_name": "relationshipDensity",
                "category": "cluster",
                "type": "float",
                "description": "Graph edge density in user 2-hop neighborhood",
                "available_at_decision_time": True,
                "uses_future_data": False
            },
            {
                "feature_name": "ringConnectivityScore",
                "category": "cluster",
                "type": "float",
                "description": "Log1p product of shared device and IP account counts",
                "available_at_decision_time": True,
                "uses_future_data": False
            }
        ]
    }
    return manifest

def run_feature_pipeline(input_csv="data/financial/financial_events.csv",
                         baseline_out=BASELINE_OUTPUT_PATH,
                         enhanced_out=ENHANCED_OUTPUT_PATH,
                         manifest_out=FEATURE_MANIFEST_PATH):
    """
    Processes transaction dataset chronologically, building entity graph state
    and generating baseline and enhanced feature outputs.
    """
    print(f"Loading input dataset from {input_csv}...")
    df = pd.read_csv(input_csv)

    # Ensure chronological sorting
    df["timestamp"] = pd.to_datetime(df["timestamp"])
    df = df.sort_values("timestamp").reset_index(drop=True)

    records = df.to_dict("records")
    graph = FinancialEntityGraph()
    extractor = FinancialFeatureExtractor()

    baseline_rows = []
    enhanced_rows = []

    start_time = time.time()

    for idx, record in enumerate(records):
        # Convert pandas Timestamp to ISO string for extractor/graph
        record["timestamp"] = str(record["timestamp"])

        # Extract features BEFORE adding to graph (or at exact decision time)
        base_feats = extractor.extract_baseline_features(record)
        enh_feats = extractor.extract_enhanced_features(record, graph)

        # Incorporate record into temporal graph
        graph.add_transaction(record)

        # Preserve transactionId and isFraud label for evaluation
        base_row = {
            "transactionId": record["transactionId"],
            "isFraud": record["isFraud"],
            **base_feats
        }

        enh_row = {
            "transactionId": record["transactionId"],
            "isFraud": record["isFraud"],
            **enh_feats
        }

        baseline_rows.append(base_row)
        enhanced_rows.append(enh_row)

    elapsed_time = time.time() - start_time
    throughput = len(records) / elapsed_time if elapsed_time > 0 else 0.0

    df_base = pd.DataFrame(baseline_rows)
    df_enh = pd.DataFrame(enhanced_rows)

    os.makedirs(os.path.dirname(baseline_out), exist_ok=True)
    df_base.to_csv(baseline_out, index=False)
    df_enh.to_csv(enhanced_out, index=False)

    manifest = get_feature_manifest()
    with open(manifest_out, "w", encoding="utf-8") as f:
        json.dump(manifest, f, indent=2)

    # Perform Exploratory Sanity Analysis comparing Legitimate vs Fraudulent
    legit_enh = df_enh[df_enh["isFraud"] == 0]
    fraud_enh = df_enh[df_enh["isFraud"] == 1]

    sanity = {
        "legit_count": len(legit_enh),
        "fraud_count": len(fraud_enh),
        "avg_sharedDeviceAccountCount": {
            "legit": float(legit_enh["sharedDeviceAccountCount"].mean()),
            "fraud": float(fraud_enh["sharedDeviceAccountCount"].mean())
        },
        "avg_sharedIpAccountCount": {
            "legit": float(legit_enh["sharedIpAccountCount"].mean()),
            "fraud": float(fraud_enh["sharedIpAccountCount"].mean())
        },
        "avg_sharedPaymentAccountCount": {
            "legit": float(legit_enh["sharedPaymentAccountCount"].mean()),
            "fraud": float(fraud_enh["sharedPaymentAccountCount"].mean())
        },
        "avg_deviceClusterSize": {
            "legit": float(legit_enh["deviceClusterSize"].mean()),
            "fraud": float(fraud_enh["deviceClusterSize"].mean())
        },
        "avg_ringConnectivityScore": {
            "legit": float(legit_enh["ringConnectivityScore"].mean()),
            "fraud": float(fraud_enh["ringConnectivityScore"].mean())
        }
    }

    print(f"\n=== Financial Feature Pipeline Complete ===")
    print(f"Processed Records : {len(records)}")
    print(f"Total Time        : {elapsed_time:.3f} seconds")
    print(f"Throughput        : {throughput:.1f} records/sec")
    print(f"Baseline Output   : {baseline_out} ({len(df_base)} rows, {len(df_base.columns)} cols)")
    print(f"Enhanced Output   : {enhanced_out} ({len(df_enh)} rows, {len(df_enh.columns)} cols)")
    print(f"Manifest Output   : {manifest_out}")
    print(f"\n--- Exploratory Sanity Analysis (Legit vs Fraud Means) ---")
    print(f"Shared Device Accounts   -> Legit: {sanity['avg_sharedDeviceAccountCount']['legit']:.2f} | Fraud: {sanity['avg_sharedDeviceAccountCount']['fraud']:.2f}")
    print(f"Shared IP Accounts       -> Legit: {sanity['avg_sharedIpAccountCount']['legit']:.2f} | Fraud: {sanity['avg_sharedIpAccountCount']['fraud']:.2f}")
    print(f"Shared Payment Accounts  -> Legit: {sanity['avg_sharedPaymentAccountCount']['legit']:.2f} | Fraud: {sanity['avg_sharedPaymentAccountCount']['fraud']:.2f}")
    print(f"Device Cluster Size      -> Legit: {sanity['avg_deviceClusterSize']['legit']:.2f} | Fraud: {sanity['avg_deviceClusterSize']['fraud']:.2f}")
    print(f"Ring Connectivity Score  -> Legit: {sanity['avg_ringConnectivityScore']['legit']:.2f} | Fraud: {sanity['avg_ringConnectivityScore']['fraud']:.2f}")

    return {
        "records": len(records),
        "elapsed_time": elapsed_time,
        "throughput": throughput,
        "sanity": sanity
    }

if __name__ == "__main__":
    run_feature_pipeline()
