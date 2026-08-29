"""
Sentinel Phase 6F — Hardened ML Experiment & Scenario-Level Evaluation

Evaluates Baseline vs Enhanced ML models on Phase 6F Hardened Financial Risk Datasets.
Includes detailed scenario-level performance breakdowns across 11 fraud & legitimate scenarios.
"""

import os
import sys
import json
import time
import pandas as pd
import numpy as np
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
from datetime import datetime, timezone
from importlib.util import module_from_spec, spec_from_file_location

from sklearn.preprocessing import StandardScaler
from sklearn.ensemble import RandomForestClassifier
from sklearn.linear_model import LogisticRegression
from sklearn.metrics import (
    precision_score, recall_score, f1_score,
    roc_auc_score, average_precision_score, confusion_matrix
)

# Load graph & feature modules
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

engine_path = os.path.abspath(os.path.join(os.path.dirname(__file__), "risk_decision_engine.py"))
e_spec = spec_from_file_location("risk_decision_engine", engine_path)
risk_decision_engine = module_from_spec(e_spec)
e_spec.loader.exec_module(risk_decision_engine)
RiskDecisionEngine = risk_decision_engine.RiskDecisionEngine

PHASE6F_DIR = "data/financial/phase6f"
EVALUATION_DIR = os.path.join(PHASE6F_DIR, "evaluation")
CHARTS_DIR = os.path.join(EVALUATION_DIR, "charts")

def extract_split_features(df_split, graph, extractor):
    records = df_split.to_dict("records")
    base_rows = []
    enh_rows = []

    for r in records:
        r["timestamp"] = str(r["timestamp"])
        base_f = extractor.extract_baseline_features(r)
        enh_f = extractor.extract_enhanced_features(r, graph)

        graph.add_transaction(r)

        base_rows.append({"transactionId": r["transactionId"], "isFraud": r["isFraud"], "fraudScenario": r["fraudScenario"], **base_f})
        enh_rows.append({"transactionId": r["transactionId"], "isFraud": r["isFraud"], "fraudScenario": r["fraudScenario"], **enh_f})

    return pd.DataFrame(base_rows), pd.DataFrame(enh_rows)

def run_phase6f_experiment(data_dir=PHASE6F_DIR, output_dir=EVALUATION_DIR, seed=42):
    os.makedirs(output_dir, exist_ok=True)
    os.makedirs(CHARTS_DIR, exist_ok=True)
    t0 = time.time()

    df_tr = pd.read_csv(os.path.join(data_dir, "train.csv"))
    df_val = pd.read_csv(os.path.join(data_dir, "validation.csv"))
    df_te = pd.read_csv(os.path.join(data_dir, "test.csv"))

    for df in [df_tr, df_val, df_te]:
        df["timestamp"] = pd.to_datetime(df["timestamp"])
        df.sort_values("timestamp", inplace=True)

    graph = FinancialEntityGraph()
    extractor = FinancialFeatureExtractor()

    base_tr, enh_tr = extract_split_features(df_tr, graph, extractor)
    base_val, enh_val = extract_split_features(df_val, graph, extractor)
    base_te, enh_te = extract_split_features(df_te, graph, extractor)

    ignore = ["transactionId", "isFraud", "fraudScenario"]
    b_cols = [c for c in base_tr.columns if c not in ignore]
    e_cols = [c for c in enh_tr.columns if c not in ignore]

    scaler_b = StandardScaler().fit(base_tr[b_cols])
    scaler_e = StandardScaler().fit(enh_tr[e_cols])

    rf_b = RandomForestClassifier(n_estimators=100, max_depth=10, random_state=seed).fit(scaler_b.transform(base_tr[b_cols]), base_tr["isFraud"])
    rf_e = RandomForestClassifier(n_estimators=100, max_depth=10, random_state=seed).fit(scaler_e.transform(enh_tr[e_cols]), enh_tr["isFraud"])

    # Threshold selection on validation
    p_val_b = rf_b.predict_proba(scaler_b.transform(base_val[b_cols]))[:, 1]
    p_val_e = rf_e.predict_proba(scaler_e.transform(enh_val[e_cols]))[:, 1]

    def find_thresh(y_true, y_probs):
        best_t, best_f1 = 0.5, -1
        for t in np.linspace(0.05, 0.95, 91):
            f1 = f1_score(y_true, (y_probs >= t).astype(int), zero_division=0)
            if f1 > best_f1:
                best_f1, best_t = f1, t
        return best_t

    t_opt_b = find_thresh(base_val["isFraud"], p_val_b)
    t_opt_e = find_thresh(enh_val["isFraud"], p_val_e)

    # Evaluate on held-out test set
    p_te_b = rf_b.predict_proba(scaler_b.transform(base_te[b_cols]))[:, 1]
    p_te_e = rf_e.predict_proba(scaler_e.transform(enh_te[e_cols]))[:, 1]

    preds_b = (p_te_b >= t_opt_b).astype(int)
    preds_e = (p_te_e >= t_opt_e).astype(int)

    base_te["prob"] = p_te_b
    base_te["pred"] = preds_b
    enh_te["prob"] = p_te_e
    enh_te["pred"] = preds_e

    # 1. Overall Model Metrics
    def calc_metrics(y_true, y_probs, preds):
        cm = confusion_matrix(y_true, preds)
        tn, fp, fn, tp = cm.ravel()
        return {
            "precision": round(precision_score(y_true, preds, zero_division=0), 4),
            "recall": round(recall_score(y_true, preds, zero_division=0), 4),
            "f1": round(f1_score(y_true, preds, zero_division=0), 4),
            "roc_auc": round(roc_auc_score(y_true, y_probs), 4),
            "pr_auc": round(average_precision_score(y_true, y_probs), 4),
            "tp": int(tp), "fp": int(fp), "tn": int(tn), "fn": int(fn)
        }

    m_b = calc_metrics(base_te["isFraud"], p_te_b, preds_b)
    m_e = calc_metrics(enh_te["isFraud"], p_te_e, preds_e)

    model_metrics_df = pd.DataFrame([
        {"model": "Random Forest", "features": "Baseline", "threshold": t_opt_b, **m_b},
        {"model": "Random Forest", "features": "Enhanced", "threshold": t_opt_e, **m_e}
    ])
    model_metrics_df.to_csv(os.path.join(output_dir, "model_metrics.csv"), index=False)

    # 2. Scenario-Level Metrics Breakdown
    scenarios = base_te["fraudScenario"].unique()
    scenario_rows = []

    for sc in scenarios:
        sub_b = base_te[base_te["fraudScenario"] == sc]
        sub_e = enh_te[enh_te["fraudScenario"] == sc]

        is_fraud = sub_b["isFraud"].iloc[0]

        rec_b = sub_b["pred"].mean() if is_fraud == 1 else (1.0 - sub_b["pred"].mean())
        rec_e = sub_e["pred"].mean() if is_fraud == 1 else (1.0 - sub_e["pred"].mean())

        fp_b = (sub_b["pred"] == 1).sum() if is_fraud == 0 else 0
        fp_e = (sub_e["pred"] == 1).sum() if is_fraud == 0 else 0

        fn_b = (sub_b["pred"] == 0).sum() if is_fraud == 1 else 0
        fn_e = (sub_e["pred"] == 0).sum() if is_fraud == 1 else 0

        scenario_rows.append({
            "scenario": sc,
            "is_fraud_label": is_fraud,
            "total_count": len(sub_b),
            "baseline_accuracy": round((sub_b["pred"] == is_fraud).mean(), 4),
            "enhanced_accuracy": round((sub_e["pred"] == is_fraud).mean(), 4),
            "baseline_detection_recall": round(rec_b, 4),
            "enhanced_detection_recall": round(rec_e, 4),
            "baseline_fp": int(fp_b),
            "enhanced_fp": int(fp_e),
            "baseline_fn": int(fn_b),
            "enhanced_fn": int(fn_e)
        })

    scenario_df = pd.DataFrame(scenario_rows).sort_values("is_fraud_label", ascending=False)
    scenario_df.to_csv(os.path.join(output_dir, "scenario_metrics.csv"), index=False)

    # 3. Cost Engine Evaluation
    engine = RiskDecisionEngine(false_positive_cost=15.0, false_negative_cost=85.0, review_cost=5.0, customer_friction_cost=2.0)

    cost_b = engine.evaluate_dataset_policy(p_te_b, base_te["isFraud"], t_review=0.20, t_block=t_opt_b)
    cost_e = engine.evaluate_dataset_policy(p_te_e, enh_te["isFraud"], t_review=0.20, t_block=t_opt_e)

    cost_df = pd.DataFrame([
        {"feature_set": "Baseline", "threshold": t_opt_b, **cost_b},
        {"feature_set": "Enhanced", "threshold": t_opt_e, **cost_e}
    ])
    cost_df.to_csv(os.path.join(output_dir, "cost_metrics.csv"), index=False)

    # 4. Feature Importance
    fi_df = pd.DataFrame({
        "feature_name": e_cols,
        "importance": rf_e.feature_importances_
    }).sort_values("importance", ascending=False)
    fi_df["feature_category"] = fi_df["feature_name"].apply(lambda x: "baseline" if x in b_cols else "graph")
    fi_df.to_csv(os.path.join(output_dir, "feature_importance.csv"), index=False)

    # 5. Charts Generation
    # Chart 1: Scenario Recall Comparison Bar Chart
    fraud_sc_df = scenario_df[scenario_df["is_fraud_label"] == 1]
    fig, ax = plt.subplots(figsize=(10, 6))
    x = np.arange(len(fraud_sc_df))
    width = 0.35
    ax.bar(x - width/2, fraud_sc_df["baseline_detection_recall"], width, label="Baseline Model", color="#1f77b4")
    ax.bar(x + width/2, fraud_sc_df["enhanced_detection_recall"], width, label="Enhanced Model (Graph)", color="#ff7f0e")
    ax.set_title("Fraud Detection Recall by Scenario (Phase 6F Hardened Test Set)", fontsize=12, fontweight="bold")
    ax.set_xticks(x)
    ax.set_xticklabels(fraud_sc_df["scenario"], rotation=25, ha="right")
    ax.set_ylabel("Detection Recall (Catch Rate)")
    ax.set_ylim(0.0, 1.05)
    ax.legend()
    fig.tight_layout()
    fig.savefig(os.path.join(CHARTS_DIR, "scenario_recall_comparison.png"), dpi=300)
    plt.close(fig)

    # Chart 2: Legitimate False Positive Comparison Bar Chart
    legit_sc_df = scenario_df[scenario_df["is_fraud_label"] == 0]
    fig, ax = plt.subplots(figsize=(8, 5))
    x = np.arange(len(legit_sc_df))
    ax.bar(x - width/2, legit_sc_df["baseline_fp"], width, label="Baseline Model", color="#1f77b4")
    ax.bar(x + width/2, legit_sc_df["enhanced_fp"], width, label="Enhanced Model (Graph)", color="#ff7f0e")
    ax.set_title("False Positives on Legitimate Scenarios (Lower is Better)", fontsize=12, fontweight="bold")
    ax.set_xticks(x)
    ax.set_xticklabels(legit_sc_df["scenario"], rotation=15)
    ax.set_ylabel("False Positive Count")
    ax.legend()
    fig.tight_layout()
    fig.savefig(os.path.join(CHARTS_DIR, "scenario_fp_comparison.png"), dpi=300)
    plt.close(fig)

    # Chart 3: Risk Score Probability Distribution
    fig, ax = plt.subplots(figsize=(8, 5))
    ax.hist(enh_te[enh_te["isFraud"] == 0]["prob"], bins=30, alpha=0.6, label="Legitimate Tx", color="green")
    ax.hist(enh_te[enh_te["isFraud"] == 1]["prob"], bins=30, alpha=0.6, label="Fraudulent Tx", color="red")
    ax.set_title("Enhanced Risk Score Probability Distribution", fontsize=12, fontweight="bold")
    ax.set_xlabel("Predicted Fraud Probability")
    ax.set_ylabel("Transaction Count")
    ax.legend()
    fig.tight_layout()
    fig.savefig(os.path.join(CHARTS_DIR, "risk_score_distribution.png"), dpi=300)
    plt.close(fig)

    # Chart 4: Expected Cost Comparison
    fig, ax = plt.subplots(figsize=(6, 5))
    ax.bar(["Baseline", "Enhanced"], [cost_b["total_cost"], cost_e["total_cost"]], color=["#1f77b4", "#ff7f0e"])
    ax.set_title("Total Expected Business Cost (Phase 6F Test Set)", fontsize=12, fontweight="bold")
    ax.set_ylabel("Total Business Cost (INR)")
    fig.tight_layout()
    fig.savefig(os.path.join(CHARTS_DIR, "expected_cost_comparison.png"), dpi=300)
    plt.close(fig)

    # 6. Benchmark Summary JSON
    biz_summary = {
        "dataset_version": "2.0.0-Phase6F-Hardened",
        "test_size": len(base_te),
        "baseline_metrics": m_b,
        "enhanced_metrics": m_e,
        "baseline_cost": cost_b["total_cost"],
        "enhanced_cost": cost_e["total_cost"],
        "cost_reduction_amount": round(cost_b["total_cost"] - cost_e["total_cost"], 2),
        "cost_reduction_percentage": round(((cost_b["total_cost"] - cost_e["total_cost"]) / cost_b["total_cost"]) * 100.0, 2) if cost_b["total_cost"] > 0 else 0.0,
        "key_scenario_gains": {
            "LOW_AND_SLOW_RING": {
                "baseline_recall": float(scenario_df[scenario_df["scenario"] == "LOW_AND_SLOW_RING"]["baseline_detection_recall"].values[0]),
                "enhanced_recall": float(scenario_df[scenario_df["scenario"] == "LOW_AND_SLOW_RING"]["enhanced_detection_recall"].values[0])
            },
            "EVASIVE_FRAUD": {
                "baseline_recall": float(scenario_df[scenario_df["scenario"] == "EVASIVE_FRAUD"]["baseline_detection_recall"].values[0]),
                "enhanced_recall": float(scenario_df[scenario_df["scenario"] == "EVASIVE_FRAUD"]["enhanced_detection_recall"].values[0])
            }
        },
        "executed_at": datetime.now(timezone.utc).isoformat()
    }

    with open(os.path.join(output_dir, "benchmark_summary.json"), "w", encoding="utf-8") as f:
        json.dump(biz_summary, f, indent=2)

    elapsed = time.time() - t0
    print(f"\n=== Phase 6F Hardened ML Experiment Complete ===")
    print(f"Total Runtime     : {elapsed:.3f} seconds")
    print(f"Baseline F1 / PR  : F1={m_b['f1']} | PR-AUC={m_b['pr_auc']} | FP={m_b['fp']} | FN={m_b['fn']} | Cost=INR {cost_b['total_cost']}")
    print(f"Enhanced F1 / PR  : F1={m_e['f1']} | PR-AUC={m_e['pr_auc']} | FP={m_e['fp']} | FN={m_e['fn']} | Cost=INR {cost_e['total_cost']}")
    print(f"\n--- Key Scenario Performance Breakdown ---")
    print(scenario_df[["scenario", "baseline_detection_recall", "enhanced_detection_recall", "baseline_fp", "enhanced_fp", "baseline_fn", "enhanced_fn"]].to_string(index=False))

    return biz_summary

if __name__ == "__main__":
    run_phase6f_experiment()
