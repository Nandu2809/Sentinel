#!/usr/bin/env python3
"""
Sentinel Phase 6F — Hardened Benchmark Execution CLI Runner
"""

import sys
import os
from importlib.util import module_from_spec, spec_from_file_location

sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), "../")))

exp_path = os.path.abspath(os.path.join(os.path.dirname(__file__), "../ai-engine/anomaly-detection/app/financial/phase6f_ml_experiment.py"))
spec = spec_from_file_location("phase6f_ml_experiment", exp_path)
phase6f_ml_experiment = module_from_spec(spec)
sys.modules["phase6f_ml_experiment"] = phase6f_ml_experiment
spec.loader.exec_module(phase6f_ml_experiment)

if __name__ == "__main__":
    phase6f_ml_experiment.run_phase6f_experiment()
