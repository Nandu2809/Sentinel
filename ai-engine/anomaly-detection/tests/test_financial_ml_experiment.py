#!/usr/bin/env python3
"""
Sentinel Phase 6D — AI Engine Test Integration for ML Experiment
"""

import sys
import os
import unittest

sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), "../../../")))
from tests.test_financial_ml_experiment import TestFinancialMLExperiment

if __name__ == "__main__":
    unittest.main()
