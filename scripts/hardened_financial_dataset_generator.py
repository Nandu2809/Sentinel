#!/usr/bin/env python3
"""
Sentinel Phase 6F — Hardened Synthetic Financial Risk Dataset Generator

Generates a realistic, controlled benchmark dataset (15,000 records) featuring:
- Hardened fraud scenarios (LOW_AND_SLOW_RING, EVASIVE_FRAUD, BORDERLINE_TRANSACTIONS, MIXED_SIGNALS)
- Legitimate shared infrastructure baseline (LEGITIMATE_SHARED_INFRASTRUCTURE: office Wi-Fi, family devices)
- Eliminates trivial separability, testing whether relationship graph features add incremental risk signal.
"""

import os
import json
import argparse
import random
from datetime import datetime, timedelta, timezone

def parse_args():
    parser = argparse.ArgumentParser(description="Sentinel Phase 6F Hardened Financial Risk Dataset Generator")
    parser.add_argument("--seed", type=int, default=42, help="Random seed for deterministic generation")
    parser.add_argument("--size", type=int, default=15000, help="Total number of synthetic transaction records")
    parser.add_argument("--fraud-rate", type=float, default=0.08, help="Target fraud prevalence (default: 0.08 = 8 percent)")
    parser.add_argument("--output-dir", type=str, default="data/financial/phase6f", help="Output directory for Phase 6F dataset")
    parser.add_argument("--train-ratio", type=float, default=0.70, help="Training split ratio (default: 0.70)")
    parser.add_argument("--val-ratio", type=float, default=0.10, help="Validation split ratio (default: 0.10)")
    parser.add_argument("--test-ratio", type=float, default=0.20, help="Held-out test split ratio (default: 0.20)")
    return parser.parse_args()

def generate_hardened_dataset(seed=42, size=15000, fraud_rate=0.08, output_dir="data/financial/phase6f",
                              train_ratio=0.70, val_ratio=0.10, test_ratio=0.20):
    random.seed(seed)
    os.makedirs(output_dir, exist_ok=True)

    fraud_count = int(size * fraud_rate)
    legit_count = size - fraud_count

    base_time = datetime(2026, 8, 1, 0, 0, 0, tzinfo=timezone.utc)

    # Pre-generate synthetic entities
    legit_users = [f"usr_norm_{i:04d}" for i in range(3000)]
    legit_devices = [f"dev_fingerprint_{i:04d}" for i in range(2000)]
    legit_ips = [f"192.168.{random.randint(1, 254)}.{random.randint(1, 254)}" for _ in range(1500)]
    shared_office_ips = [f"192.168.10.{x}" for x in range(1, 10)]  # Legitimate shared office Wi-Fi subnets
    shared_family_devices = [f"dev_family_shared_{x:03d}" for x in range(1, 30)]  # Shared family devices
    legit_merchants = [f"merch_razorpay_{i:03d}" for i in range(100)]
    legit_payment_refs = [f"pm_tok_synthetic_{i:05d}" for i in range(3500)]
    locations = [
        "Mumbai, IN", "Bengaluru, IN", "Delhi, IN", "Hyderabad, IN",
        "Chennai, IN", "Pune, IN", "Kolkata, IN", "Ahmedabad, IN"
    ]

    # Map legitimate user profiles
    user_profiles = {}
    for user_id in legit_users:
        user_profiles[user_id] = {
            "primary_device": random.choice(legit_devices),
            "primary_ip": random.choice(legit_ips),
            "primary_payment_ref": random.choice(legit_payment_refs),
            "account_age_days": random.randint(15, 1000)
        }

    records = []

    # 1. Generate Legitimate Transactions (92%)
    # Includes LEGITIMATE_SHARED_INFRASTRUCTURE (office Wi-Fi & family devices)
    for i in range(legit_count):
        user_id = random.choice(legit_users)
        prof = user_profiles[user_id]

        r_type = random.random()
        if r_type < 0.20:
            # LEGITIMATE_SHARED_INFRASTRUCTURE (Office Wi-Fi)
            ip_address = random.choice(shared_office_ips)
            device_id = prof["primary_device"]
            shared_ip_cnt = random.randint(8, 22)
            shared_dev_cnt = random.randint(1, 2)
            scenario = "LEGITIMATE_SHARED_INFRASTRUCTURE"
        elif r_type < 0.30:
            # LEGITIMATE_SHARED_INFRASTRUCTURE (Shared Family Tablet/PC)
            device_id = random.choice(shared_family_devices)
            ip_address = prof["primary_ip"]
            shared_dev_cnt = random.randint(3, 6)
            shared_ip_cnt = random.randint(1, 3)
            scenario = "LEGITIMATE_SHARED_INFRASTRUCTURE"
        else:
            # Standard Legitimate Transaction
            device_id = prof["primary_device"] if random.random() < 0.90 else f"dev_fingerprint_{random.randint(2000, 2499):04d}"
            ip_address = prof["primary_ip"] if random.random() < 0.85 else f"10.0.{random.randint(1, 254)}.{random.randint(1, 254)}"
            shared_dev_cnt = random.choices([1, 2, 3], weights=[0.85, 0.12, 0.03])[0]
            shared_ip_cnt = random.choices([1, 2, 3, 4, 5], weights=[0.60, 0.25, 0.10, 0.04, 0.01])[0]
            scenario = "NONE"

        payment_ref = prof["primary_payment_ref"] if random.random() < 0.95 else random.choice(legit_payment_refs)
        merchant_id = random.choice(legit_merchants)
        location = random.choice(locations)

        amount = round(random.lognormvariate(6.2, 0.7), 2)
        amount = max(50.0, min(amount, 12000.0))

        offset_seconds = random.randint(0, 28 * 24 * 3600)
        timestamp = base_time + timedelta(seconds=offset_seconds)

        velocity1h = random.choices([1, 2, 3, 4], weights=[0.75, 0.18, 0.05, 0.02])[0]
        failed_tx_24h = random.choices([0, 1, 2], weights=[0.90, 0.08, 0.02])[0]

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
            "sharedDeviceAccountCount": shared_dev_cnt,
            "sharedIpAccountCount": shared_ip_cnt,
            "isFraud": 0,
            "fraudScenario": scenario
        })

    # 2. Generate Hardened Fraud Scenarios (8%)
    fraud_scenarios_list = [
        "DEVICE_SHARING_RING",
        "SHARED_IP_BURST",
        "PAYMENT_REF_REUSE",
        "BEHAVIORAL_ANOMALY",
        "COMBINED_RING",
        "LOW_AND_SLOW_RING",
        "EVASIVE_FRAUD",
        "BORDERLINE_TRANSACTIONS",
        "MIXED_SIGNALS"
    ]
    fraud_per_scenario = fraud_count // len(fraud_scenarios_list)
    fraud_id_counter = 0

    # A. DEVICE_SHARING_RING
    for k in range(fraud_per_scenario):
        fraud_id_counter += 1
        records.append({
            "transactionId": f"tx_fraud_{fraud_id_counter:06d}",
            "userId": f"usr_ringA_{k % 25:03d}",
            "merchantId": random.choice(legit_merchants),
            "amount": round(random.uniform(2500.0, 18000.0), 2),
            "currency": "INR",
            "timestamp": (base_time + timedelta(seconds=random.randint(0, 28 * 24 * 3600))).isoformat(),
            "deviceId": f"dev_fraud_ringA_{k % 5}",
            "ipAddress": f"198.51.100.{random.randint(10, 90)}",
            "location": random.choice(locations),
            "paymentMethodRef": f"pm_tok_ringA_{k % 10:03d}",
            "accountAgeDays": random.randint(1, 14),
            "velocity1h": random.randint(4, 12),
            "failedTxCount24h": random.randint(1, 4),
            "sharedDeviceAccountCount": random.randint(8, 18),
            "sharedIpAccountCount": random.randint(2, 6),
            "isFraud": 1,
            "fraudScenario": "DEVICE_SHARING_RING"
        })

    # B. SHARED_IP_BURST
    for k in range(fraud_per_scenario):
        fraud_id_counter += 1
        records.append({
            "transactionId": f"tx_fraud_{fraud_id_counter:06d}",
            "userId": f"usr_burstB_{k % 30:03d}",
            "merchantId": random.choice(legit_merchants),
            "amount": round(random.uniform(1200.0, 15000.0), 2),
            "currency": "INR",
            "timestamp": (base_time + timedelta(seconds=random.randint(0, 28 * 24 * 3600))).isoformat(),
            "deviceId": f"dev_burstB_{k % 20:03d}",
            "ipAddress": f"203.0.113.{101 + (k % 5)}",
            "location": random.choice(locations),
            "paymentMethodRef": f"pm_tok_burstB_{k % 15:03d}",
            "accountAgeDays": random.randint(2, 30),
            "velocity1h": random.randint(8, 24),
            "failedTxCount24h": random.randint(2, 8),
            "sharedDeviceAccountCount": random.randint(1, 4),
            "sharedIpAccountCount": random.randint(12, 35),
            "isFraud": 1,
            "fraudScenario": "SHARED_IP_BURST"
        })

    # C. PAYMENT_REF_REUSE
    for k in range(fraud_per_scenario):
        fraud_id_counter += 1
        records.append({
            "transactionId": f"tx_fraud_{fraud_id_counter:06d}",
            "userId": f"usr_reuseC_{k % 20:03d}",
            "merchantId": random.choice(legit_merchants),
            "amount": round(random.uniform(3000.0, 22000.0), 2),
            "currency": "INR",
            "timestamp": (base_time + timedelta(seconds=random.randint(0, 28 * 24 * 3600))).isoformat(),
            "deviceId": f"dev_reuseC_{k % 20:03d}",
            "ipAddress": f"198.51.100.{100 + (k % 10)}",
            "location": random.choice(locations),
            "paymentMethodRef": f"pm_tok_stolenC_{k % 5}",
            "accountAgeDays": random.randint(0, 5),
            "velocity1h": random.randint(3, 9),
            "failedTxCount24h": random.randint(0, 3),
            "sharedDeviceAccountCount": random.randint(2, 5),
            "sharedIpAccountCount": random.randint(2, 6),
            "isFraud": 1,
            "fraudScenario": "PAYMENT_REF_REUSE"
        })

    # D. BEHAVIORAL_ANOMALY
    for k in range(fraud_per_scenario):
        fraud_id_counter += 1
        records.append({
            "transactionId": f"tx_fraud_{fraud_id_counter:06d}",
            "userId": f"usr_anomD_{k:03d}",
            "merchantId": random.choice(legit_merchants),
            "amount": round(random.uniform(45000.0, 95000.0), 2),
            "currency": "INR",
            "timestamp": (base_time + timedelta(seconds=random.randint(0, 28 * 24 * 3600))).isoformat(),
            "deviceId": f"dev_anomD_{k:03d}",
            "ipAddress": f"192.0.2.{random.randint(1, 254)}",
            "location": "VPN Exit Node",
            "paymentMethodRef": f"pm_tok_anomD_{k:03d}",
            "accountAgeDays": random.randint(1, 10),
            "velocity1h": random.randint(6, 16),
            "failedTxCount24h": random.randint(3, 7),
            "sharedDeviceAccountCount": 1,
            "sharedIpAccountCount": 1,
            "isFraud": 1,
            "fraudScenario": "BEHAVIORAL_ANOMALY"
        })

    # E. COMBINED_RING
    for k in range(fraud_per_scenario):
        fraud_id_counter += 1
        records.append({
            "transactionId": f"tx_fraud_{fraud_id_counter:06d}",
            "userId": f"usr_combE_{k % 20:03d}",
            "merchantId": random.choice(legit_merchants),
            "amount": round(random.uniform(15000.0, 65000.0), 2),
            "currency": "INR",
            "timestamp": (base_time + timedelta(seconds=random.randint(0, 28 * 24 * 3600))).isoformat(),
            "deviceId": f"dev_combE_{k % 3}",
            "ipAddress": f"203.0.113.{201 + (k % 3)}",
            "location": "Tor Exit Node",
            "paymentMethodRef": f"pm_tok_combE_{k % 3}",
            "accountAgeDays": random.randint(0, 7),
            "velocity1h": random.randint(10, 30),
            "failedTxCount24h": random.randint(4, 10),
            "sharedDeviceAccountCount": random.randint(10, 25),
            "sharedIpAccountCount": random.randint(15, 40),
            "isFraud": 1,
            "fraudScenario": "COMBINED_RING"
        })

    # F. LOW_AND_SLOW_RING (Crucial: Low amount, low velocity = Baseline model misses, Graph catches!)
    for k in range(fraud_per_scenario):
        fraud_id_counter += 1
        records.append({
            "transactionId": f"tx_fraud_{fraud_id_counter:06d}",
            "userId": f"usr_lowslowF_{k % 30:03d}",
            "merchantId": random.choice(legit_merchants),
            "amount": round(random.uniform(800.0, 2500.0), 2),  # Completely normal amount!
            "currency": "INR",
            "timestamp": (base_time + timedelta(seconds=random.randint(0, 28 * 24 * 3600))).isoformat(),
            "deviceId": f"dev_lowslowF_{k % 4}",  # Highly shared device across 30 low-and-slow fraud accounts
            "ipAddress": f"198.51.100.{50 + (k % 5)}",
            "location": random.choice(locations),
            "paymentMethodRef": f"pm_tok_lowslowF_{k % 15:03d}",
            "accountAgeDays": random.randint(60, 300),  # Aged account!
            "velocity1h": 1,  # Perfectly normal velocity!
            "failedTxCount24h": 0,  # Zero failed attempts!
            "sharedDeviceAccountCount": random.randint(12, 30),  # Elevated device sharing!
            "sharedIpAccountCount": random.randint(8, 20),
            "isFraud": 1,
            "fraudScenario": "LOW_AND_SLOW_RING"
        })

    # G. EVASIVE_FRAUD (Crucial: Normal amount, normal velocity, normal account age = Baseline misses!)
    for k in range(fraud_per_scenario):
        fraud_id_counter += 1
        records.append({
            "transactionId": f"tx_fraud_{fraud_id_counter:06d}",
            "userId": f"usr_evasiveG_{k % 25:03d}",
            "merchantId": random.choice(legit_merchants),
            "amount": round(random.uniform(1200.0, 3500.0), 2),
            "currency": "INR",
            "timestamp": (base_time + timedelta(seconds=random.randint(0, 28 * 24 * 3600))).isoformat(),
            "deviceId": f"dev_evasiveG_{k % 25:03d}",
            "ipAddress": f"203.0.113.{10 + (k % 5)}",
            "location": random.choice(locations),
            "paymentMethodRef": f"pm_tok_evasiveG_{k % 3}",  # Shared payment reference across evasive ring!
            "accountAgeDays": random.randint(90, 450),
            "velocity1h": 1,
            "failedTxCount24h": 0,
            "sharedDeviceAccountCount": 1,
            "sharedIpAccountCount": random.randint(10, 25),
            "isFraud": 1,
            "fraudScenario": "EVASIVE_FRAUD"
        })

    # H. BORDERLINE_TRANSACTIONS
    for k in range(fraud_per_scenario):
        fraud_id_counter += 1
        records.append({
            "transactionId": f"tx_fraud_{fraud_id_counter:06d}",
            "userId": f"usr_borderlineH_{k % 20:03d}",
            "merchantId": random.choice(legit_merchants),
            "amount": round(random.uniform(3000.0, 7500.0), 2),
            "currency": "INR",
            "timestamp": (base_time + timedelta(seconds=random.randint(0, 28 * 24 * 3600))).isoformat(),
            "deviceId": f"dev_borderlineH_{k % 5}",
            "ipAddress": f"198.51.100.{20 + (k % 5)}",
            "location": random.choice(locations),
            "paymentMethodRef": f"pm_tok_borderlineH_{k % 10:03d}",
            "accountAgeDays": random.randint(30, 180),
            "velocity1h": 2,
            "failedTxCount24h": 1,
            "sharedDeviceAccountCount": random.randint(6, 15),
            "sharedIpAccountCount": random.randint(5, 12),
            "isFraud": 1,
            "fraudScenario": "BORDERLINE_TRANSACTIONS"
        })

    # I. MIXED_SIGNALS (High account age = 500 days, but high device graph sharing)
    rem_fraud = fraud_count - (8 * fraud_per_scenario)
    for k in range(rem_fraud):
        fraud_id_counter += 1
        records.append({
            "transactionId": f"tx_fraud_{fraud_id_counter:06d}",
            "userId": f"usr_mixedI_{k % 20:03d}",
            "merchantId": random.choice(legit_merchants),
            "amount": round(random.uniform(2000.0, 8500.0), 2),
            "currency": "INR",
            "timestamp": (base_time + timedelta(seconds=random.randint(0, 28 * 24 * 3600))).isoformat(),
            "deviceId": f"dev_mixedI_{k % 3}",
            "ipAddress": f"203.0.113.{150 + (k % 4)}",
            "location": random.choice(locations),
            "paymentMethodRef": f"pm_tok_mixedI_{k % 5:03d}",
            "accountAgeDays": random.randint(400, 900),  # Mixed signal: Old account!
            "velocity1h": 2,
            "failedTxCount24h": 0,
            "sharedDeviceAccountCount": random.randint(10, 22),  # Shared device ring!
            "sharedIpAccountCount": random.randint(8, 18),
            "isFraud": 1,
            "fraudScenario": "MIXED_SIGNALS"
        })

    # Sort all records chronologically
    records.sort(key=lambda x: x["timestamp"])

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

    import csv
    def write_csv(filepath, rows):
        with open(filepath, "w", newline="", encoding="utf-8") as f:
            writer = csv.DictWriter(f, fieldnames=fieldnames)
            writer.writeheader()
            writer.writerows(rows)

    write_csv(os.path.join(output_dir, "financial_events_hardened.csv"), records)
    write_csv(os.path.join(output_dir, "train.csv"), train_records)
    write_csv(os.path.join(output_dir, "validation.csv"), val_records)
    write_csv(os.path.join(output_dir, "test.csv"), test_records)

    scenario_counts = {}
    for r in records:
        sc = r["fraudScenario"]
        scenario_counts[sc] = scenario_counts.get(sc, 0) + 1

    metadata = {
        "generator_version": "2.0.0-Phase6F-Hardened",
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
        "scenarios": scenario_counts,
        "created_at": datetime.now(timezone.utc).isoformat()
    }

    with open(os.path.join(output_dir, "dataset_metadata.json"), "w", encoding="utf-8") as f:
        json.dump(metadata, f, indent=2)

    print(f"=== Phase 6F Hardened Financial Risk Dataset Generated ===")
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
    generate_hardened_dataset(
        seed=args.seed,
        size=args.size,
        fraud_rate=args.fraud_rate,
        output_dir=args.output_dir,
        train_ratio=args.train_ratio,
        val_ratio=args.val_ratio,
        test_ratio=args.test_ratio
    )
