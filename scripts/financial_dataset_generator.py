#!/usr/bin/env python3
"""
Sentinel Phase 6B — Synthetic Financial Risk Dataset Generator

Generates a deterministic, seed-driven synthetic financial risk dataset
for evaluating Coordinated Payment Abuse & Fraud-Ring Detection models.

Strictly contains NO real payment credentials (cards, CVVs, OTPs, or passwords).
All user IDs, payment references, device fingerprints, and IPs are synthetic/tokenized.
"""

import os
import json
import argparse
import random
from datetime import datetime, timedelta, timezone

def parse_args():
    parser = argparse.ArgumentParser(description="Sentinel Synthetic Financial Risk Dataset Generator")
    parser.add_argument("--seed", type=int, default=42, help="Random seed for deterministic generation")
    parser.add_argument("--size", type=int, default=10000, help="Total number of synthetic transaction records")
    parser.add_argument("--fraud-rate", type=float, default=0.08, help="Target fraud prevalence (default: 0.08 = 8 percent)")
    parser.add_argument("--output-dir", type=str, default="data/financial", help="Output directory for generated CSVs and metadata")
    parser.add_argument("--train-ratio", type=float, default=0.70, help="Ratio for training dataset split (default: 0.70)")
    parser.add_argument("--val-ratio", type=float, default=0.10, help="Ratio for validation dataset split (default: 0.10)")
    parser.add_argument("--test-ratio", type=float, default=0.20, help="Ratio for held-out test dataset split (default: 0.20)")
    return parser.parse_args()

def generate_dataset(seed=42, size=10000, fraud_rate=0.08, output_dir="data/financial", train_ratio=0.70, val_ratio=0.10, test_ratio=0.20):
    # Set seeds for complete determinism
    random.seed(seed)

    os.makedirs(output_dir, exist_ok=True)

    fraud_count = int(size * fraud_rate)
    legit_count = size - fraud_count

    # Pre-generate synthetic entities
    legit_users = [f"usr_norm_{i:04d}" for i in range(2000)]
    legit_devices = [f"dev_fingerprint_{i:04d}" for i in range(1500)]
    legit_ips = [f"192.168.{random.randint(1, 254)}.{random.randint(1, 254)}" for _ in range(1000)]
    legit_merchants = [f"merch_razorpay_{i:03d}" for i in range(50)]
    legit_payment_refs = [f"pm_tok_synthetic_{i:05d}" for i in range(2500)]
    locations = [
        "Mumbai, IN", "Bengaluru, IN", "Delhi, IN", "Hyderabad, IN",
        "Chennai, IN", "Pune, IN", "Kolkata, IN", "Ahmedabad, IN"
    ]

    base_time = datetime(2026, 8, 1, 0, 0, 0, tzinfo=timezone.utc)
    records = []

    # Map legitimate users to primary devices/IPs for realistic consistency
    user_profiles = {}
    for user_id in legit_users:
        user_profiles[user_id] = {
            "primary_device": random.choice(legit_devices),
            "primary_ip": random.choice(legit_ips),
            "primary_payment_ref": random.choice(legit_payment_refs),
            "account_age_days": random.randint(30, 900)
        }

    # 1. Generate Legitimate Transactions (92%)
    for i in range(legit_count):
        user_id = random.choice(legit_users)
        prof = user_profiles[user_id]

        # 90% primary device, 10% secondary/new device
        device_id = prof["primary_device"] if random.random() < 0.90 else f"dev_fingerprint_{random.randint(1500, 1999):04d}"
        ip_address = prof["primary_ip"] if random.random() < 0.85 else f"10.0.{random.randint(1, 254)}.{random.randint(1, 254)}"
        payment_ref = prof["primary_payment_ref"] if random.random() < 0.95 else random.choice(legit_payment_refs)
        merchant_id = random.choice(legit_merchants)
        location = random.choice(locations)

        # Realistic amount distribution (log-normal between 100 and 5000 INR)
        amount = round(random.lognormvariate(6.2, 0.7), 2)
        amount = max(50.0, min(amount, 12000.0))

        # Time distribution across 28 days
        offset_seconds = random.randint(0, 28 * 24 * 3600)
        timestamp = base_time + timedelta(seconds=offset_seconds)

        # Legitimate features (with natural slight overlap to avoid trivial classification)
        velocity1h = random.choices([1, 2, 3, 4], weights=[0.75, 0.18, 0.05, 0.02])[0]
        failed_tx_24h = random.choices([0, 1, 2], weights=[0.90, 0.08, 0.02])[0]
        shared_device_count = random.choices([1, 2, 3], weights=[0.85, 0.12, 0.03])[0]
        shared_ip_count = random.choices([1, 2, 3, 4, 5], weights=[0.60, 0.25, 0.10, 0.04, 0.01])[0]

        records.append({
            "transactionId": f"tx_legit_{i:06d}",
            "userId": user_id,
            "merchantId": merchant_id,
            "amount": amount,
            "currency": "INR",
            "timestamp": timestamp.isoformat(),
            "deviceId": device_id,
            "ipAddress": ip_address,
            "location": location,
            "paymentMethodRef": payment_ref,
            "accountAgeDays": prof["account_age_days"],
            "velocity1h": velocity1h,
            "failedTxCount24h": failed_tx_24h,
            "sharedDeviceAccountCount": shared_device_count,
            "sharedIpAccountCount": shared_ip_count,
            "isFraud": 0,
            "fraudScenario": "NONE"
        })

    # 2. Generate Fraud Scenarios (8%)
    scenarios = [
        "DEVICE_SHARING_RING",
        "SHARED_IP_BURST",
        "PAYMENT_REF_REUSE",
        "BEHAVIORAL_ANOMALY",
        "COMBINED_RING"
    ]
    fraud_per_scenario = fraud_count // len(scenarios)

    fraud_id_counter = 0

    # SCENARIO A: DEVICE_SHARING_RING
    ring_a_devices = [f"dev_fraud_ring_A_{k}" for k in range(5)]
    for k in range(fraud_per_scenario):
        fraud_id_counter += 1
        device_id = random.choice(ring_a_devices)
        user_id = f"usr_fraud_ringA_{k % 25:03d}"
        offset_seconds = random.randint(5 * 24 * 3600, 20 * 24 * 3600)
        timestamp = base_time + timedelta(seconds=offset_seconds)

        records.append({
            "transactionId": f"tx_fraud_{fraud_id_counter:06d}",
            "userId": user_id,
            "merchantId": random.choice(legit_merchants),
            "amount": round(random.uniform(2500.0, 18000.0), 2),
            "currency": "INR",
            "timestamp": timestamp.isoformat(),
            "deviceId": device_id,
            "ipAddress": f"198.51.100.{random.randint(10, 90)}",
            "location": random.choice(locations),
            "paymentMethodRef": f"pm_tok_ringA_{random.randint(1, 10):03d}",
            "accountAgeDays": random.randint(1, 14),
            "velocity1h": random.randint(4, 12),
            "failedTxCount24h": random.randint(1, 4),
            "sharedDeviceAccountCount": random.randint(7, 18),
            "sharedIpAccountCount": random.randint(2, 6),
            "isFraud": 1,
            "fraudScenario": "DEVICE_SHARING_RING"
        })

    # SCENARIO B: SHARED_IP_BURST
    ring_b_ips = [f"203.0.113.{x}" for x in range(101, 106)]
    for k in range(fraud_per_scenario):
        fraud_id_counter += 1
        ip_address = random.choice(ring_b_ips)
        user_id = f"usr_fraud_burstB_{k % 30:03d}"
        offset_seconds = random.randint(10 * 24 * 3600, 25 * 24 * 3600)
        timestamp = base_time + timedelta(seconds=offset_seconds)

        records.append({
            "transactionId": f"tx_fraud_{fraud_id_counter:06d}",
            "userId": user_id,
            "merchantId": random.choice(legit_merchants),
            "amount": round(random.uniform(1200.0, 15000.0), 2),
            "currency": "INR",
            "timestamp": timestamp.isoformat(),
            "deviceId": f"dev_fraud_burstB_{random.randint(1, 40):03d}",
            "ipAddress": ip_address,
            "location": random.choice(locations),
            "paymentMethodRef": f"pm_tok_burstB_{random.randint(1, 20):03d}",
            "accountAgeDays": random.randint(2, 30),
            "velocity1h": random.randint(8, 24),
            "failedTxCount24h": random.randint(2, 8),
            "sharedDeviceAccountCount": random.randint(1, 4),
            "sharedIpAccountCount": random.randint(12, 35),
            "isFraud": 1,
            "fraudScenario": "SHARED_IP_BURST"
        })

    # SCENARIO C: PAYMENT_REF_REUSE
    stolen_payment_refs = [f"pm_tok_stolen_C_{x:03d}" for x in range(1, 6)]
    for k in range(fraud_per_scenario):
        fraud_id_counter += 1
        payment_ref = random.choice(stolen_payment_refs)
        user_id = f"usr_fraud_reuseC_{k % 20:03d}"
        offset_seconds = random.randint(2 * 24 * 3600, 22 * 24 * 3600)
        timestamp = base_time + timedelta(seconds=offset_seconds)

        records.append({
            "transactionId": f"tx_fraud_{fraud_id_counter:06d}",
            "userId": user_id,
            "merchantId": random.choice(legit_merchants),
            "amount": round(random.uniform(3000.0, 22000.0), 2),
            "currency": "INR",
            "timestamp": timestamp.isoformat(),
            "deviceId": f"dev_reuseC_{random.randint(1, 25):03d}",
            "ipAddress": f"198.51.100.{random.randint(100, 200)}",
            "location": random.choice(locations),
            "paymentMethodRef": payment_ref,
            "accountAgeDays": random.randint(0, 5),
            "velocity1h": random.randint(3, 9),
            "failedTxCount24h": random.randint(0, 3),
            "sharedDeviceAccountCount": random.randint(2, 5),
            "sharedIpAccountCount": random.randint(2, 6),
            "isFraud": 1,
            "fraudScenario": "PAYMENT_REF_REUSE"
        })

    # SCENARIO D: BEHAVIORAL_ANOMALY
    for k in range(fraud_per_scenario):
        fraud_id_counter += 1
        user_id = f"usr_fraud_anomD_{k:03d}"
        offset_seconds = random.randint(1 * 24 * 3600, 27 * 24 * 3600)
        timestamp = base_time + timedelta(seconds=offset_seconds)

        records.append({
            "transactionId": f"tx_fraud_{fraud_id_counter:06d}",
            "userId": user_id,
            "merchantId": random.choice(legit_merchants),
            "amount": round(random.uniform(35000.0, 95000.0), 2),  # Unusually high amount spike
            "currency": "INR",
            "timestamp": timestamp.isoformat(),
            "deviceId": f"dev_anomD_{k:03d}",
            "ipAddress": f"192.0.2.{random.randint(1, 254)}",
            "location": "Unknown / VPN Node",
            "paymentMethodRef": f"pm_tok_anomD_{k:03d}",
            "accountAgeDays": random.randint(1, 10),
            "velocity1h": random.randint(6, 16),
            "failedTxCount24h": random.randint(3, 7),
            "sharedDeviceAccountCount": random.randint(1, 3),
            "sharedIpAccountCount": random.randint(1, 4),
            "isFraud": 1,
            "fraudScenario": "BEHAVIORAL_ANOMALY"
        })

    # SCENARIO E: COMBINED_RING
    comb_devices = [f"dev_fraud_combE_{x}" for x in range(3)]
    comb_ips = [f"203.0.113.{x}" for x in range(201, 204)]
    comb_payment_refs = [f"pm_tok_combE_{x}" for x in range(3)]

    # Remaining fraud count for Scenario E
    rem_e_count = fraud_count - (4 * fraud_per_scenario)
    for k in range(rem_e_count):
        fraud_id_counter += 1
        user_id = f"usr_fraud_combE_{k % 25:03d}"
        offset_seconds = random.randint(15 * 24 * 3600, 28 * 24 * 3600)
        timestamp = base_time + timedelta(seconds=offset_seconds)

        records.append({
            "transactionId": f"tx_fraud_{fraud_id_counter:06d}",
            "userId": user_id,
            "merchantId": random.choice(legit_merchants),
            "amount": round(random.uniform(15000.0, 65000.0), 2),
            "currency": "INR",
            "timestamp": timestamp.isoformat(),
            "deviceId": random.choice(comb_devices),
            "ipAddress": random.choice(comb_ips),
            "location": "Tor / Proxy Exit Node",
            "paymentMethodRef": random.choice(comb_payment_refs),
            "accountAgeDays": random.randint(0, 7),
            "velocity1h": random.randint(10, 30),
            "failedTxCount24h": random.randint(4, 10),
            "sharedDeviceAccountCount": random.randint(10, 25),
            "sharedIpAccountCount": random.randint(15, 40),
            "isFraud": 1,
            "fraudScenario": "COMBINED_RING"
        })

    # Sort all records chronologically by timestamp
    records.sort(key=lambda x: x["timestamp"])

    # Perform Train / Validation / Test Split (70% / 10% / 20%)
    total_records = len(records)
    train_end = int(total_records * train_ratio)
    val_end = train_end + int(total_records * val_ratio)

    train_records = records[:train_end]
    val_records = records[train_end:val_end]
    test_records = records[val_end:]

    fieldnames = [
        "transactionId", "userId", "merchantId", "amount", "currency",
        "timestamp", "deviceId", "ipAddress", "location", "paymentMethodRef",
        "accountAgeDays", "velocity1h", "failedTxCount24h",
        "sharedDeviceAccountCount", "sharedIpAccountCount", "isFraud", "fraudScenario"
    ]

    # Helper function to write CSV
    def write_csv(filepath, rows):
        import csv
        with open(filepath, "w", newline="", encoding="utf-8") as f:
            writer = csv.DictWriter(f, fieldnames=fieldnames)
            writer.writeheader()
            writer.writerows(rows)

    write_csv(os.path.join(output_dir, "financial_events.csv"), records)
    write_csv(os.path.join(output_dir, "train.csv"), train_records)
    write_csv(os.path.join(output_dir, "validation.csv"), val_records)
    write_csv(os.path.join(output_dir, "test.csv"), test_records)

    # Calculate scenario statistics
    scenario_counts = {}
    for r in records:
        sc = r["fraudScenario"]
        scenario_counts[sc] = scenario_counts.get(sc, 0) + 1

    metadata = {
        "generator_version": "1.0.0",
        "seed": seed,
        "records": total_records,
        "fraud_rate": fraud_rate,
        "legitimate_count": sum(1 for r in records if r["isFraud"] == 0),
        "fraud_count": sum(1 for r in records if r["isFraud"] == 1),
        "train_count": len(train_records),
        "validation_count": len(val_records),
        "test_count": len(test_records),
        "train_fraud_count": sum(1 for r in train_records if r["isFraud"] == 1),
        "val_fraud_count": sum(1 for r in val_records if r["isFraud"] == 1),
        "test_fraud_count": sum(1 for r in test_records if r["isFraud"] == 1),
        "features": fieldnames[:-2],
        "target": "isFraud",
        "metadata_only_fields": ["fraudScenario"],
        "fraud_scenarios": scenario_counts,
        "created_at": datetime.now(timezone.utc).isoformat()
    }

    with open(os.path.join(output_dir, "dataset_metadata.json"), "w", encoding="utf-8") as f:
        json.dump(metadata, f, indent=2)

    print(f"=== Synthetic Financial Risk Dataset Generated ===")
    print(f"Directory   : {output_dir}")
    print(f"Total Rows  : {total_records}")
    print(f"Legitimate  : {metadata['legitimate_count']}")
    print(f"Fraudulent  : {metadata['fraud_count']} ({metadata['fraud_rate']*100:.1f}%)")
    print(f"Train Rows  : {len(train_records)} (Fraud: {metadata['train_fraud_count']})")
    print(f"Val Rows    : {len(val_records)} (Fraud: {metadata['val_fraud_count']})")
    print(f"Test Rows   : {len(test_records)} (Fraud: {metadata['test_fraud_count']})")
    print(f"Scenarios   : {json.dumps(scenario_counts, indent=2)}")

if __name__ == "__main__":
    args = parse_args()
    generate_dataset(
        seed=args.seed,
        size=args.size,
        fraud_rate=args.fraud_rate,
        output_dir=args.output_dir,
        train_ratio=args.train_ratio,
        val_ratio=args.val_ratio,
        test_ratio=args.test_ratio
    )
