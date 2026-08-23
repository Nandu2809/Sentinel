export type Role = 'ADMIN' | 'SECURITY_ANALYST' | 'USER';

export interface AuthUser {
  id: string;
  name: string;
  email: string;
  role: Role;
  riskScore: number;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  user: AuthUser;
}

export type Severity = 'SAFE' | 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';

export interface SecurityEvent {
  id: string;
  timestamp: string;
  type: string;
  actor: string;
  location: string;
  riskScore: number;
  severity: Severity;
  description?: string;
}

export interface Threat {
  id: string;
  category: string;
  confidence: number; // 0-100, AI detection confidence
  severity: Severity;
  firstSeen: string;
  lastSeen: string;
  pattern: string;
  occurrences: number;
}

export interface RiskFactor {
  label: string;
  weight: number; // contribution to score
  direction: 'up' | 'down';
}

export interface UserRiskProfile {
  userId: string;
  name: string;
  score: number; // 0-100
  trend: number[]; // historical scores
  factors: RiskFactor[];
}

export type AlertStatus = 'NEW' | 'INVESTIGATING' | 'ACKNOWLEDGED' | 'RESOLVED';

export interface SecurityAlert {
  id: string;
  incidentNumber: string;
  threat: string;
  severity: Severity;
  riskScore: number;
  status: AlertStatus;
  assignedTo?: string;
  createdAt: string;
  description: string;
}

export interface SystemHealthMetric {
  label: string;
  value: number; // percentage
  status: Severity;
}
