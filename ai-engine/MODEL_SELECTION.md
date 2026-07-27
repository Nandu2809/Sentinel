# Model Selection Rationale

## Baseline Machine Learning Algorithms

### 1. Isolation Forest
- **Purpose**: Multivariate Outlier Detection.
- **Why Selected**: Isolates anomalies by randomly partitioning feature space. Requires minimal assumptions about data distribution and executes with $O(n \log n)$ time complexity.
- **Weight in Ensemble**: **45%**

### 2. One-Class SVM (OC-SVM)
- **Purpose**: Decision Boundary Classification.
- **Why Selected**: Fits a tight non-linear boundary around legitimate API traffic using RBF kernel. Ideal for detecting subtle shifts in user behavior.
- **Weight in Ensemble**: **35%**

### 3. Density/Distance Clustering
- **Purpose**: Unknown Attack Pattern Grouping.
- **Why Selected**: Groups unclassified anomalies together, helping analysts discover zero-day vulnerability exploitation campaigns.
- **Weight in Ensemble**: **20%**
