#!/usr/bin/env python3
"""
Sentinel Phase 6D — Baseline vs Enhanced ML Experiment CLI Runner
"""

import sys
import os
from importlib.util import module_from_spec, spec_from_file_location

# Add repo root
sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), "../")))

exp_path = os.path.abspath(os.path.join(os.path.dirname(__file__), "../ai-engine/anomaly-detection/app/financial/financial_ml_experiment.py"))
spec = spec_from_file_location("financial_ml_experiment", exp_path)
financial_ml_experiment = module_from_spec(spec)
sys.modules["financial_ml_experiment"] = financial_ml_experiment
spec.loader.exec_module(financial_ml_experiment)

if __name__ == "__main__":
    financial_ml_experiment.run_ml_experiment()
