import pytest
import numpy as np
from app.models.isolation_forest import IsolationForestDetector
from app.models.one_class_svm import OneClassSVMDetector
from app.models.clustering import ClusterAnomalyDetector

def test_models_training_and_prediction():
    X_train = np.random.normal(3.0, 0.5, size=(100, 11))
    X_test_normal = np.random.normal(3.0, 0.5, size=(1, 11))
    X_test_outlier = np.random.normal(15.0, 2.0, size=(1, 11))

    # Isolation Forest
    iso = IsolationForestDetector()
    iso.train(X_train)
    norm_score, norm_flag = iso.predict(X_test_normal)
    out_score, out_flag = iso.predict(X_test_outlier)
    assert out_score > norm_score

    # One Class SVM
    svm = OneClassSVMDetector()
    svm.train(X_train)
    svm_norm_score, _ = svm.predict(X_test_normal)
    svm_out_score, _ = svm.predict(X_test_outlier)
    assert svm_out_score > svm_norm_score

    # Clustering
    cls = ClusterAnomalyDetector()
    cls.train(X_train)
    cls_norm_score, _ = cls.predict(X_test_normal)
    cls_out_score, _ = cls.predict(X_test_outlier)
    assert cls_out_score > cls_norm_score
