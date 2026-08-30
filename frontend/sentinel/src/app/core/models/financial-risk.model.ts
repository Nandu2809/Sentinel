export type DecisionType = 'APPROVE' | 'REVIEW' | 'BLOCK' | 'BLOCK_AND_ALERT';
export type RiskLevelType = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
export type EntityType = 'USER' | 'DEVICE' | 'IP' | 'PAYMENT' | 'MERCHANT';

export interface FinancialRiskDecision {
  id: string;
  userId: string;
  threatId: string; // Used as Transaction ID or Threat Ref
  riskScore: number;
  riskProbability?: number;
  riskLevel: RiskLevelType;
  decision: DecisionType;
  createdAt: string;
  // Enhanced telemetry attributes
  amount?: number;
  currency?: string;
  merchantId?: string;
  deviceId?: string;
  ipAddress?: string;
  location?: string;
  paymentMethodRef?: string;
  accountAgeDays?: number;
  velocity1h?: number;
  failedTxCount24h?: number;
  sharedDeviceAccountCount?: number;
  sharedIpAccountCount?: number;
  riskFactors?: string[];
  topSignal?: string;
  modelVersion?: string;
  expectedCost?: number;
}

export interface FinancialSummary {
  totalEvaluated: number;
  approveCount: number;
  reviewCount: number;
  blockCount: number;
  highRiskRate: number;
  expectedLoss: number;
  fraudDetectionRate: number;
  falsePositiveRate: number;
}

export interface GraphNode {
  id: string;
  label: string;
  type: EntityType;
  x?: number;
  y?: number;
  suspicious: boolean;
  degree: number;
  activity: string;
  details?: Record<string, any>;
}

export interface GraphEdge {
  source: string;
  target: string;
  label: string;
  suspicious: boolean;
}

export interface GraphTopology {
  centerEntity: GraphNode;
  nodes: GraphNode[];
  edges: GraphEdge[];
}

export interface ScenarioMetric {
  scenario: string;
  label: string;
  description: string;
  baselineRecall: number;
  enhancedRecall: number;
  diff: number;
  baselineFP: number;
  enhancedFP: number;
  category: 'GRAPH_GAIN' | 'BASELINE_STRONGER' | 'NEUTRAL';
}

export interface ModelBenchmark {
  architecture: string;
  featureSet: 'Baseline (9 Features)' | 'Enhanced (34 Features)';
  precision: number;
  recall: number;
  f1: number;
  rocAuc: number;
  prAuc: number;
  fp: number;
  fn: number;
  expectedCost: number;
}

export interface BusinessCostSummary {
  costFP: number;
  costFN: number;
  costReview: number;
  costFriction: number;
  expectedTotalCost: number;
  approveRate: number;
  reviewRate: number;
  blockRate: number;
  policyRationale: string;
}

export interface RiskTimelineStage {
  step: number;
  name: string;
  stageKey: string;
  timestamp: string;
  status: 'SUCCESS' | 'WARNING' | 'ALERT' | 'PENDING';
  value: string;
}

export interface ActivityConsoleLog {
  id: string;
  timestamp: string;
  type: 'EVENT_RECEIVED' | 'RISK_EVALUATED' | 'GRAPH_SIGNAL' | 'DECISION_GENERATED' | 'ALERT_CREATED' | 'INCIDENT_CREATED';
  message: string;
  severity: 'INFO' | 'WARN' | 'CRITICAL';
}
