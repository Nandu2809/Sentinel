"""
Sentinel Phase 6D — Baseline vs Enhanced ML Risk Engine Experiment

Scientific comparison of Baseline vs Graph-Enhanced ML fraud detection models
on held-out test data with strict temporal anti-leakage controls.
"""

import os
import sys
import json
import time
import pandas as pd
import numpy as np
from datetime import datetime, timezone
from importlib.util import module_from_spec, spec_from_file_location

from sklearn.preprocessing import StandardScaler
from sklearn.linear_model import LogisticRegression
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import (
    precision_score, recall_score, f1_score,
    roc_auc_score, average_precision_score, confusion_matrix
)

# Load Phase 6C modules
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

EVALUATION_DIR = "data/financial/evaluation"

def extract_split_features(df_split, graph, extractor):
    """
    Extracts baseline and enhanced features chronologically for a dataset split.
    Updates the temporal entity graph as transactions are processed.
    """
    records = df_split.to_dict("records")
    base_rows = []
    enh_rows = []

    for r in records:
        r["timestamp"] = str(r["timestamp"])

        base_feats = extractor.extract_baseline_features(r)
        enh_feats = extractor.extract_enhanced_features(r, graph)

        graph.add_transaction(r)

        base_rows.append({
            "transactionId": r["transactionId"],
            "isFraud": r["isFraud"],
            **base_feats
        })

        enh_rows.append({
            "transactionId": r["transactionId"],
            "isFraud": r["isFraud"],
            **enh_feats
        })

    return pd.DataFrame(base_rows), pd.DataFrame(enh_rows)

def find_optimal_threshold(y_true, y_probs):
    """
    Selects decision threshold optimizing F1 score on validation dataset.
    """
    best_thresh = 0.5
    best_f1 = -1.0

    thresholds = np.linspace(0.05, 0.95, 91)
    for t in thresholds:
        preds = (y_probs >= t).astype(int)
        f1 = f1_score(y_true, preds, zero_division=0)
        if f1 > best_f1:
            best_f1 = f1
            best_thresh = t

    return best_thresh, best_f1

def evaluate_model_on_test(model, scaler, X_test, y_test, threshold):
    """Evaluates trained model on held-out test data using validation-selected threshold."""
    X_test_scaled = scaler.transform(X_test)
    y_probs = model.predict_proba(X_test_scaled)[:, 1]
    y_preds = (y_probs >= threshold).astype(int)

    cm = confusion_matrix(y_test, y_preds)
    tn, fp, fn, tp = cm.ravel()

    prec = precision_score(y_test, y_preds, zero_division=0)
    rec = recall_score(y_test, y_preds, zero_division=0)
    f1 = f1_score(y_test, y_preds, zero_division=0)
    roc_auc = roc_auc_score(y_test, y_probs)
    pr_auc = average_precision_score(y_test, y_probs)

    return {
        "precision": round(prec, 4),
        "recall": round(rec, 4),
        "f1": round(f1, 4),
        "roc_auc": round(roc_auc, 4),
        "pr_auc": round(pr_auc, 4),
        "tp": int(tp),
        "fp": int(fp),
        "tn": int(tn),
        "fn": int(fn),
        "fraud_recall": round(rec, 4),
        "threshold": round(threshold, 4),
        "cm": cm.tolist()
    }

def run_ml_experiment(data_dir="data/financial", output_dir=EVALUATION_DIR, seed=42):
    """Runs complete Phase 6D Baseline vs Enhanced ML fraud experiment."""
    os.makedirs(output_dir, exist_ok=True)
    start_time = time.time()

    # 1. Load dataset splits
    print("Loading train, validation, and test dataset splits...")
    df_train = pd.read_csv(os.path.join(data_dir, "train.csv"))
    df_val = pd.read_csv(os.path.join(data_dir, "validation.csv"))
    df_test = pd.read_csv(os.path.join(data_dir, "test.csv"))

    # Ensure chronological sorting
    for df in [df_train, df_val, df_test]:
        df["timestamp"] = pd.to_datetime(df["timestamp"])
        df.sort_values("timestamp", inplace=True)

    # 2. Extract features sequentially across splits maintaining single graph timeline
    print("Extracting features with temporal entity graph state...")
    graph = FinancialEntityGraph()
    extractor = FinancialFeatureExtractor()

    base_train, enh_train = extract_split_features(df_train, graph, extractor)
    base_val, enh_val = extract_split_features(df_val, graph, extractor)
    base_test, enh_test = extract_split_features(df_test, graph, extractor)

    # Prepare feature matrices & labels
    ignore_cols = ["transactionId", "isFraud", "fraudScenario"]

    base_cols = [c for c in base_train.columns if c not in ignore_cols]
    enh_cols = [c for c in enh_train.columns if c not in ignore_cols]

    # Verify zero label leakage
    assert "isFraud" not in base_cols and "fraudScenario" not in base_cols
    assert "isFraud" not in enh_cols and "fraudScenario" not in enh_cols

    X_tr_base, y_tr_base = base_train[base_cols], base_train["isFraud"]
    X_val_base, y_val_base = base_val[base_cols], base_val["isFraud"]
    X_te_base, y_te_base = base_test[base_cols], base_test["isFraud"]

    X_tr_enh, y_tr_enh = enh_train[enh_cols], enh_train["isFraud"]
    X_val_enh, y_val_enh = enh_val[enh_cols], enh_val["isFraud"]
    X_te_enh, y_te_enh = enh_test[enh_cols], enh_test["isFraud"]

    # 3. Model Training & Validation Threshold Tuning

    # --- Logistic Regression ---
    print("Training Logistic Regression models...")
    scaler_lr_base = StandardScaler().fit(X_tr_base)
    scaler_lr_enh = StandardScaler().fit(X_tr_enh)

    lr_base = LogisticRegression(random_state=seed, max_iter=1000).fit(scaler_lr_base.transform(X_tr_base), y_tr_base)
    lr_enh = LogisticRegression(random_state=seed, max_iter=1000).fit(scaler_lr_enh.transform(X_tr_enh), y_tr_enh)

    # Select threshold on validation set
    lr_base_val_probs = lr_base.predict_proba(scaler_lr_base.transform(X_val_base))[:, 1]
    lr_enh_val_probs = lr_enh.predict_proba(scaler_lr_enh.transform(X_val_enh))[:, 1]

    thresh_lr_base, _ = find_optimal_threshold(y_val_base, lr_base_val_probs)
    thresh_lr_enh, _ = find_optimal_threshold(y_val_enh, lr_enh_val_probs)

    # Evaluate once on test set
    res_lr_base = evaluate_model_on_test(lr_base, scaler_lr_base, X_te_base, y_te_base, thresh_lr_base)
    res_lr_enh = evaluate_model_on_test(lr_enh, scaler_lr_enh, X_te_enh, y_te_enh, thresh_lr_enh)

    # --- Random Forest ---
    print("Training Random Forest Classifier models...")
    scaler_rf_base = StandardScaler().fit(X_tr_base)
    scaler_rf_enh = StandardScaler().fit(X_tr_enh)

    rf_base = RandomForestClassifier(n_estimators=100, max_depth=10, random_state=seed).fit(scaler_rf_base.transform(X_tr_base), y_tr_base)
    rf_enh = RandomForestClassifier(n_estimators=100, max_depth=10, random_state=seed).fit(scaler_rf_enh.transform(X_tr_enh), y_tr_enh)

    rf_base_val_probs = rf_base.predict_proba(scaler_rf_base.transform(X_val_base))[:, 1]
    rf_enh_val_probs = rf_enh.predict_proba(scaler_rf_enh.transform(X_val_enh))[:, 1]

    thresh_rf_base, _ = find_optimal_threshold(y_val_base, rf_base_val_probs)
    thresh_rf_enh, _ = find_optimal_threshold(y_val_enh, rf_enh_val_probs)

    res_rf_base = evaluate_model_on_test(rf_base, scaler_rf_base, X_te_base, y_te_base, thresh_rf_base)
    res_rf_enh = evaluate_model_on_test(rf_enh, scaler_rf_enh, X_te_enh, y_te_enh, thresh_rf_enh)

    # 4. Feature Importance Analysis
    fi_df = pd.DataFrame({
        "feature_name": enh_cols,
        "importance": rf_enh.feature_importances_
    }).sort_values("importance", ascending=False)

    fi_df["feature_category"] = fi_df["feature_name"].apply(
        lambda x: "transaction/baseline" if x in base_cols else "relationship/graph"
    )
    fi_df.to_csv(os.path.join(output_dir, "feature_importance.csv"), index=False)

    # 5. Ablation Analysis (Random Forest Model)
    print("Performing feature ablation study...")
    device_cols = [c for c in enh_cols if "device" in c.lower() or "device" in c]
    ip_cols = [c for c in enh_cols if "ip" in c.lower() or "ip" in c]
    pm_cols = [c for c in enh_cols if "payment" in c.lower() or "payment" in c]

    ablation_configs = {
        "Baseline": base_cols,
        "Baseline + Device Graph": list(set(base_cols + device_cols)),
        "Baseline + IP Graph": list(set(base_cols + ip_cols)),
        "Baseline + Payment Graph": list(set(base_cols + pm_cols)),
        "Baseline + All Graph (Enhanced)": enh_cols
    }

    ablation_results = []
    for cfg_name, cols in ablation_configs.items():
        sc = StandardScaler().fit(enh_train[cols])
        m = RandomForestClassifier(n_estimators=100, max_depth=10, random_state=seed).fit(sc.transform(enh_train[cols]), y_tr_enh)
        v_probs = m.predict_proba(sc.transform(enh_val[cols]))[:, 1]
        t_opt, _ = find_optimal_threshold(y_val_enh, v_probs)
        t_res = evaluate_model_on_test(m, sc, enh_test[cols], y_te_enh, t_opt)

        ablation_results.append({
            "configuration": cfg_name,
            "feature_count": len(cols),
            "precision": t_res["precision"],
            "recall": t_res["recall"],
            "f1": t_res["f1"],
            "roc_auc": t_res["roc_auc"],
            "pr_auc": t_res["pr_auc"],
            "fp": t_res["fp"],
            "fn": t_res["fn"]
        })

    ablation_df = pd.DataFrame(ablation_results)
    ablation_df.to_csv(os.path.join(output_dir, "ablation_results.csv"), index=False)

    # 6. Save Comparison Table & Confusion Matrices
    comparison_data = [
        {
            "model": "Logistic Regression",
            "features": "Baseline",
            "precision": res_lr_base["precision"],
            "recall": res_lr_base["recall"],
            "f1": res_lr_base["f1"],
            "roc_auc": res_lr_base["roc_auc"],
            "pr_auc": res_lr_base["pr_auc"],
            "fp": res_lr_base["fp"],
            "fn": res_lr_base["fn"]
        },
        {
            "model": "Logistic Regression",
            "features": "Enhanced",
            "precision": res_lr_enh["precision"],
            "recall": res_lr_enh["recall"],
            "f1": res_lr_enh["f1"],
            "roc_auc": res_lr_enh["roc_auc"],
            "pr_auc": res_lr_enh["pr_auc"],
            "fp": res_lr_enh["fp"],
            "fn": res_lr_enh["fn"]
        },
        {
            "model": "Random Forest",
            "features": "Baseline",
            "precision": res_rf_base["precision"],
            "recall": res_rf_base["recall"],
            "f1": res_rf_base["f1"],
            "roc_auc": res_rf_base["roc_auc"],
            "pr_auc": res_rf_base["pr_auc"],
            "fp": res_rf_base["fp"],
            "fn": res_rf_base["fn"]
        },
        {
            "model": "Random Forest",
            "features": "Enhanced",
            "precision": res_rf_enh["precision"],
            "recall": res_rf_enh["recall"],
            "f1": res_rf_enh["f1"],
            "roc_auc": res_rf_enh["roc_auc"],
            "pr_auc": res_rf_enh["pr_auc"],
            "fp": res_rf_enh["fp"],
            "fn": res_rf_enh["fn"]
        }
    ]

    comp_df = pd.DataFrame(comparison_data)
    comp_df.to_csv(os.path.join(output_dir, "comparison.csv"), index=False)

    # Confusion matrices
    cm_base_df = pd.DataFrame(res_rf_base["cm"], index=["Actual_Legit", "Actual_Fraud"], columns=["Pred_Legit", "Pred_Fraud"])
    cm_enh_df = pd.DataFrame(res_rf_enh["cm"], index=["Actual_Legit", "Actual_Fraud"], columns=["Pred_Legit", "Pred_Fraud"])

    cm_base_df.to_csv(os.path.join(output_dir, "confusion_matrix_baseline.csv"))
    cm_enh_df.to_csv(os.path.join(output_dir, "confusion_matrix_enhanced.csv"))

    # Save JSON metrics
    with open(os.path.join(output_dir, "baseline_model_metrics.json"), "w") as f:
        json.dump({"logistic_regression": res_lr_base, "random_forest": res_rf_base}, f, indent=2)

    with open(os.path.join(output_dir, "enhanced_model_metrics.json"), "w") as f:
        json.dump({"logistic_regression": res_lr_enh, "random_forest": res_rf_enh}, f, indent=2)

    elapsed_time = time.time() - start_time

    # Save Experiment Config
    exp_config = {
        "seed": seed,
        "train_size": len(df_train),
        "val_size": len(df_val),
        "test_size": len(df_test),
        "baseline_feature_count": len(base_cols),
        "enhanced_feature_count": len(enh_cols),
        "lr_threshold_baseline": res_lr_base["threshold"],
        "lr_threshold_enhanced": res_lr_enh["threshold"],
        "rf_threshold_baseline": res_rf_base["threshold"],
        "rf_threshold_enhanced": res_rf_enh["threshold"],
        "experiment_duration_sec": round(elapsed_time, 3),
        "executed_at": datetime.now(timezone.utc).isoformat()
    }
    with open(os.path.join(output_dir, "experiment_config.json"), "w") as f:
        json.dump(exp_config, f, indent=2)

    print("\n=== Phase 6D ML Experiment Completed ===")
    print(f"Total Experiment Runtime: {elapsed_time:.3f} seconds\n")
    print(comp_df.to_string(index=False))

    return {
        "comparison": comparison_data,
        "ablation": ablation_results,
        "config": exp_config
    }

if __name__ == "__main__":
    run_ml_experiment()
