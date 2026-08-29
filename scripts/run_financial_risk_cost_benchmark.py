#!/usr/bin/env python3
"""
Sentinel Phase 6E — Financial Risk Cost Benchmark & Decision Optimization Runner
"""

import os
import sys
import json
import time
import pandas as pd
import numpy as np
import matplotlib
matplotlib.use("Agg")  # Non-interactive backend
import matplotlib.pyplot as plt
from datetime import datetime, timezone
from importlib.util import module_from_spec, spec_from_file_location

# Import Phase 6D experiment & 6E risk decision engine
exp_path = os.path.abspath(os.path.join(os.path.dirname(__file__), "../ai-engine/anomaly-detection/app/financial/financial_ml_experiment.py"))
spec = spec_from_file_location("financial_ml_experiment", exp_path)
financial_ml_experiment = module_from_spec(spec)
spec.loader.exec_module(financial_ml_experiment)

engine_path = os.path.abspath(os.path.join(os.path.dirname(__file__), "../ai-engine/anomaly-detection/app/financial/risk_decision_engine.py"))
e_spec = spec_from_file_location("risk_decision_engine", engine_path)
risk_decision_engine = module_from_spec(e_spec)
sys.modules["risk_decision_engine"] = risk_decision_engine
e_spec.loader.exec_module(risk_decision_engine)
RiskDecisionEngine = risk_decision_engine.RiskDecisionEngine

EVALUATION_DIR = "data/financial/evaluation"
CHARTS_DIR = os.path.join(EVALUATION_DIR, "charts")

def generate_benchmark_charts(sweep_df, sensitivity_df, comp_cost_df, output_dir=CHARTS_DIR):
    """Generates and saves 7 separate evaluation figures using Matplotlib."""
    os.makedirs(output_dir, exist_ok=True)

    plt.style.use("seaborn-v0_8-whitegrid" if "seaborn-v0_8-whitegrid" in plt.style.available else "default")

    # 1. Threshold vs Expected Cost
    fig, ax = plt.subplots(figsize=(8, 5))
    ax.plot(sweep_df["threshold"], sweep_df["baseline_cost"], label="Baseline Model", color="#1f77b4", linewidth=2)
    ax.plot(sweep_df["threshold"], sweep_df["enhanced_cost"], label="Enhanced Model", color="#ff7f0e", linewidth=2, linestyle="--")
    ax.set_title("Decision Threshold vs Expected Business Cost", fontsize=12, fontweight="bold")
    ax.set_xlabel("Decision Threshold (t_block)")
    ax.set_ylabel("Expected Total Cost (INR)")
    ax.legend()
    fig.tight_layout()
    fig.savefig(os.path.join(output_dir, "01_threshold_vs_expected_cost.png"), dpi=300)
    plt.close(fig)

    # 2. Threshold vs False Positive Rate
    fig, ax = plt.subplots(figsize=(8, 5))
    ax.plot(sweep_df["threshold"], sweep_df["fp_rate"], color="#d62728", linewidth=2)
    ax.set_title("Decision Threshold vs False Positive Rate", fontsize=12, fontweight="bold")
    ax.set_xlabel("Decision Threshold")
    ax.set_ylabel("False Positive Rate")
    fig.tight_layout()
    fig.savefig(os.path.join(output_dir, "02_threshold_vs_fp_rate.png"), dpi=300)
    plt.close(fig)

    # 3. Threshold vs False Negative Rate
    fig, ax = plt.subplots(figsize=(8, 5))
    ax.plot(sweep_df["threshold"], sweep_df["fn_rate"], color="#2ca02c", linewidth=2)
    ax.set_title("Decision Threshold vs False Negative Rate", fontsize=12, fontweight="bold")
    ax.set_xlabel("Decision Threshold")
    ax.set_ylabel("False Negative Rate")
    fig.tight_layout()
    fig.savefig(os.path.join(output_dir, "03_threshold_vs_fn_rate.png"), dpi=300)
    plt.close(fig)

    # 4. Threshold vs Review Rate
    fig, ax = plt.subplots(figsize=(8, 5))
    ax.plot(sweep_df["threshold"], sweep_df["review_rate"], color="#9467bd", linewidth=2)
    ax.set_title("Decision Threshold vs Analyst Review Rate", fontsize=12, fontweight="bold")
    ax.set_xlabel("Decision Threshold")
    ax.set_ylabel("Review Rate (Proportion of Tx)")
    fig.tight_layout()
    fig.savefig(os.path.join(output_dir, "04_threshold_vs_review_rate.png"), dpi=300)
    plt.close(fig)

    # 5. Threshold vs Fraud Recall
    fig, ax = plt.subplots(figsize=(8, 5))
    ax.plot(sweep_df["threshold"], sweep_df["fraud_recall"], color="#8c564b", linewidth=2)
    ax.set_title("Decision Threshold vs Fraud Recall", fontsize=12, fontweight="bold")
    ax.set_xlabel("Decision Threshold")
    ax.set_ylabel("Fraud Recall")
    fig.tight_layout()
    fig.savefig(os.path.join(output_dir, "05_threshold_vs_fraud_recall.png"), dpi=300)
    plt.close(fig)

    # 6. Cost Sensitivity Curves
    fig, ax = plt.subplots(figsize=(8, 5))
    for sc in sensitivity_df["scenario"].unique():
        sub = sensitivity_df[sensitivity_df["scenario"] == sc]
        ax.plot(sub["false_negative_cost"], sub["optimal_threshold"], marker="o", label=f"Scenario {sc}")
    ax.set_title("FN Cost vs Validation Optimal Decision Threshold", fontsize=12, fontweight="bold")
    ax.set_xlabel("False Negative Cost (C_FN in INR)")
    ax.set_ylabel("Optimal Decision Threshold (t_block)")
    ax.legend()
    fig.tight_layout()
    fig.savefig(os.path.join(output_dir, "06_cost_sensitivity_curves.png"), dpi=300)
    plt.close(fig)

    # 7. Baseline vs Enhanced Expected Cost Bar Chart
    fig, ax = plt.subplots(figsize=(8, 5))
    modes = comp_cost_df["operating_mode"].unique()
    x = np.arange(len(modes))
    width = 0.35

    base_costs = comp_cost_df[comp_cost_df["feature_set"] == "Baseline"]["total_cost"].values
    enh_costs = comp_cost_df[comp_cost_df["feature_set"] == "Enhanced"]["total_cost"].values

    ax.bar(x - width/2, base_costs, width, label="Baseline", color="#1f77b4")
    ax.bar(x + width/2, enh_costs, width, label="Enhanced", color="#ff7f0e")
    ax.set_title("Expected Business Cost by Operating Mode (Held-Out Test Set)", fontsize=12, fontweight="bold")
    ax.set_xticks(x)
    ax.set_xticklabels(modes, rotation=15)
    ax.set_ylabel("Total Business Cost (INR)")
    ax.legend()
    fig.tight_layout()
    fig.savefig(os.path.join(output_dir, "07_baseline_vs_enhanced_expected_cost.png"), dpi=300)
    plt.close(fig)

def run_risk_cost_benchmark(data_dir="data/financial", eval_dir=EVALUATION_DIR, seed=42):
    """Executes Phase 6E Risk Cost Benchmark & Decision Optimization."""
    os.makedirs(eval_dir, exist_ok=True)
    start_time = time.time()

    # Illustrative Cost Structure
    default_engine = RiskDecisionEngine(
        false_positive_cost=15.0,
        false_negative_cost=85.0,
        review_cost=5.0,
        customer_friction_cost=2.0
    )

    # Load splits & extract features via Phase 6D pipeline
    df_tr = pd.read_csv(os.path.join(data_dir, "train.csv"))
    df_val = pd.read_csv(os.path.join(data_dir, "validation.csv"))
    df_te = pd.read_csv(os.path.join(data_dir, "test.csv"))

    for df in [df_tr, df_val, df_te]:
        df["timestamp"] = pd.to_datetime(df["timestamp"])
        df.sort_values("timestamp", inplace=True)

    g = financial_ml_experiment.FinancialEntityGraph()
    ext = financial_ml_experiment.FinancialFeatureExtractor()

    base_tr, enh_tr = financial_ml_experiment.extract_split_features(df_tr, g, ext)
    base_val, enh_val = financial_ml_experiment.extract_split_features(df_val, g, ext)
    base_te, enh_te = financial_ml_experiment.extract_split_features(df_te, g, ext)

    ignore = ["transactionId", "isFraud", "fraudScenario"]
    b_cols = [c for c in base_tr.columns if c not in ignore]
    e_cols = [c for c in enh_tr.columns if c not in ignore]

    # Train Random Forest models
    scaler_b = financial_ml_experiment.StandardScaler().fit(base_tr[b_cols])
    scaler_e = financial_ml_experiment.StandardScaler().fit(enh_tr[e_cols])

    rf_b = financial_ml_experiment.RandomForestClassifier(n_estimators=100, max_depth=10, random_state=seed).fit(scaler_b.transform(base_tr[b_cols]), base_tr["isFraud"])
    rf_e = financial_ml_experiment.RandomForestClassifier(n_estimators=100, max_depth=10, random_state=seed).fit(scaler_e.transform(enh_tr[e_cols]), enh_tr["isFraud"])

    # Predict probabilities
    p_val_b = rf_b.predict_proba(scaler_b.transform(base_val[b_cols]))[:, 1]
    p_val_e = rf_e.predict_proba(scaler_e.transform(enh_val[e_cols]))[:, 1]

    p_te_b = rf_b.predict_proba(scaler_b.transform(base_te[b_cols]))[:, 1]
    p_te_e = rf_e.predict_proba(scaler_e.transform(enh_te[e_cols]))[:, 1]

    # 1. Deterministic Threshold Sweep on Validation Set
    sweep_rows = []
    thresholds = np.linspace(0.05, 0.95, 91)

    for t in thresholds:
        res_b = default_engine.evaluate_dataset_policy(p_val_b, base_val["isFraud"], t_review=0.20, t_block=t)
        res_e = default_engine.evaluate_dataset_policy(p_val_e, enh_val["isFraud"], t_review=0.20, t_block=t)

        sweep_rows.append({
            "threshold": round(t, 4),
            "baseline_cost": res_b["total_cost"],
            "enhanced_cost": res_e["total_cost"],
            "fp_rate": round(res_b["fp_count"] / len(base_val), 4),
            "fn_rate": round(res_b["fn_count"] / len(base_val), 4),
            "review_rate": res_b["review_rate"],
            "fraud_recall": res_b["fraud_recall"]
        })
    sweep_df = pd.DataFrame(sweep_rows)

    # 2. Select Operating Points from Validation Data
    # Modes:
    # 1. Fraud-Minimization: low t_block (0.15)
    # 2. Customer-Friction: high t_block (0.85)
    # 3. Balanced: default t_block (0.50)
    # 4. Cost-Optimal: threshold minimizing total cost on validation set
    best_t_b = sweep_df.loc[sweep_df["baseline_cost"].idxmin()]["threshold"]
    best_t_e = sweep_df.loc[sweep_df["enhanced_cost"].idxmin()]["threshold"]

    modes_cfg = [
        ("FRAUD_MINIMIZATION", 0.15),
        ("CUSTOMER_FRICTION", 0.85),
        ("BALANCED", 0.50),
        ("COST_OPTIMAL", best_t_b)
    ]

    policy_results = []
    for mode_name, t_blk in modes_cfg:
        # Evaluate ONCE on held-out test set
        res_b_te = default_engine.evaluate_dataset_policy(p_te_b, base_te["isFraud"], t_review=0.20, t_block=t_blk)
        res_e_te = default_engine.evaluate_dataset_policy(p_te_e, enh_te["isFraud"], t_review=0.20, t_block=t_blk)

        for feat_set, res in [("Baseline", res_b_te), ("Enhanced", res_e_te)]:
            policy_results.append({
                "operating_mode": mode_name,
                "feature_set": feat_set,
                "t_review": 0.20,
                "t_block": t_blk,
                "approval_rate": res["approval_rate"],
                "review_rate": res["review_rate"],
                "block_rate": res["block_rate"],
                "fraud_recall": res["fraud_recall"],
                "precision": res["precision"],
                "false_positives": res["fp_count"],
                "false_negatives": res["fn_count"],
                "review_count": res["review_count"],
                "fp_cost": res["fp_cost"],
                "fn_cost": res["fn_cost"],
                "review_cost": res["review_cost"],
                "friction_cost": res["friction_cost"],
                "total_cost": res["total_cost"],
                "cost_per_1000": res["cost_per_1000"]
            })

    policy_df = pd.DataFrame(policy_results)
    policy_df.to_csv(os.path.join(eval_dir, "risk_policy_results.csv"), index=False)

    # 3. Cost Sensitivity Analysis
    scenarios = [
        ("Scenario A", 5.0, 50.0, 3.0, 1.0),
        ("Scenario B", 15.0, 85.0, 5.0, 2.0),
        ("Scenario C", 30.0, 100.0, 8.0, 3.0),
        ("Scenario D", 50.0, 150.0, 10.0, 5.0)
    ]

    sensitivity_rows = []
    for sc_name, cfp, cfn, crev, cfric in scenarios:
        engine_sc = RiskDecisionEngine(
            false_positive_cost=cfp,
            false_negative_cost=cfn,
            review_cost=crev,
            customer_friction_cost=cfric
        )

        # Sweep on validation to find optimal threshold for this scenario
        best_t = 0.50
        min_c = float("inf")

        for t in thresholds:
            r = engine_sc.evaluate_dataset_policy(p_val_b, base_val["isFraud"], t_review=0.20, t_block=t)
            if r["total_cost"] < min_c:
                min_c = r["total_cost"]
                best_t = t

        # Evaluate on test set
        res_sc = engine_sc.evaluate_dataset_policy(p_te_b, base_te["isFraud"], t_review=0.20, t_block=best_t)

        sensitivity_rows.append({
            "scenario": sc_name,
            "false_positive_cost": cfp,
            "false_negative_cost": cfn,
            "review_cost": crev,
            "friction_cost": cfric,
            "optimal_threshold": round(best_t, 4),
            "fraud_recall": res_sc["fraud_recall"],
            "false_positive_rate": round(res_sc["fp_count"] / len(base_te), 4),
            "review_rate": res_sc["review_rate"],
            "total_cost": res_sc["total_cost"],
            "cost_per_1000": res_sc["cost_per_1000"]
        })

    sensitivity_df = pd.DataFrame(sensitivity_rows)
    sensitivity_df.to_csv(os.path.join(eval_dir, "cost_sensitivity.csv"), index=False)

    # 4. Generate Visualizations
    generate_benchmark_charts(sweep_df, sensitivity_df, policy_df)

    # 5. Create Business Summary JSON
    cost_b_opt = policy_df[(policy_df["operating_mode"] == "COST_OPTIMAL") & (policy_df["feature_set"] == "Baseline")]["total_cost"].values[0]
    cost_e_opt = policy_df[(policy_df["operating_mode"] == "COST_OPTIMAL") & (policy_df["feature_set"] == "Enhanced")]["total_cost"].values[0]
    cost_diff = round(cost_b_opt - cost_e_opt, 2)
    cost_red_pct = round((cost_diff / cost_b_opt) * 100.0, 2) if cost_b_opt > 0 else 0.0

    biz_summary = {
        "best_cost_policy": "COST_OPTIMAL",
        "baseline_cost": float(cost_b_opt),
        "enhanced_cost": float(cost_e_opt),
        "cost_difference": float(cost_diff),
        "cost_reduction_percentage": float(cost_red_pct),
        "fraud_recall": float(policy_df[(policy_df["operating_mode"] == "COST_OPTIMAL") & (policy_df["feature_set"] == "Baseline")]["fraud_recall"].values[0]),
        "false_positive_rate": float(round(policy_df[(policy_df["operating_mode"] == "COST_OPTIMAL") & (policy_df["feature_set"] == "Baseline")]["false_positives"].values[0] / len(df_te), 4)),
        "review_rate": float(policy_df[(policy_df["operating_mode"] == "COST_OPTIMAL") & (policy_df["feature_set"] == "Baseline")]["review_rate"].values[0]),
        "recommended_threshold": float(best_t_b)
    }

    with open(os.path.join(eval_dir, "business_summary.json"), "w") as f:
        json.dump(biz_summary, f, indent=2)

    elapsed_time = time.time() - start_time

    # Config JSON
    phase6e_cfg = {
        "random_seed": seed,
        "train_size": len(df_tr),
        "val_size": len(df_val),
        "test_size": len(df_te),
        "cost_parameters": {
            "false_positive_cost": 15.0,
            "false_negative_cost": 85.0,
            "review_cost": 5.0,
            "customer_friction_cost": 2.0
        },
        "operating_modes": [m[0] for m in modes_cfg],
        "optimal_threshold_baseline": float(best_t_b),
        "optimal_threshold_enhanced": float(best_t_e),
        "benchmark_duration_sec": round(elapsed_time, 3),
        "executed_at": datetime.now(timezone.utc).isoformat()
    }

    with open(os.path.join(eval_dir, "phase6e_config.json"), "w") as f:
        json.dump(phase6e_cfg, f, indent=2)

    print("\n=== Phase 6E Risk Cost Benchmark Completed ===")
    print(f"Total Benchmark Runtime: {elapsed_time:.3f} seconds\n")
    print(policy_df[["operating_mode", "feature_set", "approval_rate", "review_rate", "block_rate", "fraud_recall", "total_cost"]].to_string(index=False))

    return {
        "policy_results": policy_results,
        "sensitivity": sensitivity_rows,
        "summary": biz_summary
    }

if __name__ == "__main__":
    run_risk_cost_benchmark()
