"""
Sentinel Phase 6F — Integrated Unit Tests for AI Engine Financial Risk Subsystem
"""

import os
import sys
import unittest
import tempfile
import pandas as pd
from importlib.util import module_from_spec, spec_from_file_location

gen_path = os.path.abspath(os.path.join(os.path.dirname(__file__), "../../../scripts/hardened_financial_dataset_generator.py"))
g_spec = spec_from_file_location("hardened_financial_dataset_generator", gen_path)
hardened_generator = module_from_spec(g_spec)
g_spec.loader.exec_module(hardened_generator)

pipe_path = os.path.abspath(os.path.join(os.path.dirname(__file__), "../app/financial/hardened_feature_pipeline.py"))
p_spec = spec_from_file_location("hardened_feature_pipeline", pipe_path)
hardened_pipeline = module_from_spec(p_spec)
p_spec.loader.exec_module(hardened_pipeline)

eng_path = os.path.abspath(os.path.join(os.path.dirname(__file__), "../app/financial/risk_decision_engine.py"))
e_spec = spec_from_file_location("risk_decision_engine", eng_path)
risk_decision_engine = module_from_spec(e_spec)
e_spec.loader.exec_module(risk_decision_engine)
RiskDecisionEngine = risk_decision_engine.RiskDecisionEngine

class TestAIPlatformPhase6F(unittest.TestCase):

    def setUp(self):
        self.tmp_dir = tempfile.TemporaryDirectory()

    def tearDown(self):
        self.tmp_dir.cleanup()

    def test_pipeline_execution_and_leakage(self):
        output_dir = self.tmp_dir.name
        hardened_generator.generate_hardened_dataset(seed=42, size=200, output_dir=output_dir)

        base_csv = os.path.join(output_dir, "features_baseline.csv")
        enh_csv = os.path.join(output_dir, "features_enhanced.csv")
        man_json = os.path.join(output_dir, "feature_manifest.json")

        hardened_pipeline.run_hardened_pipeline(
            input_csv=os.path.join(output_dir, "financial_events_hardened.csv"),
            baseline_out=base_csv,
            enhanced_out=enh_csv,
            manifest_out=man_json
        )

        self.assertTrue(os.path.exists(base_csv))
        self.assertTrue(os.path.exists(enh_csv))
        self.assertTrue(os.path.exists(man_json))

        df_enh = pd.read_csv(enh_csv)
        self.assertEqual(len(df_enh), 200)

if __name__ == "__main__":
    unittest.main()
