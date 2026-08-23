import { Severity } from './security.model';

export type IncidentStatus = 'OPEN' | 'ACKNOWLEDGED' | 'INVESTIGATING' | 'MITIGATED' | 'RESOLVED' | 'CLOSED';

export type IncidentActionType = 'ACKNOWLEDGE' | 'ASSIGN' | 'BLOCK_IP' | 'DISABLE_USER' | 'RESET_PASSWORD' | 'RESOLVE';

export interface IncidentTimelineEvent {
  id: string;
  incidentId: string;
  eventType: string;
  source: string;
  summary: string;
  detailsJson?: string;
  timestamp: string;
}

export interface IncidentAction {
  id: string;
  incidentId: string;
  actionType: IncidentActionType;
  performedBy: string;
  target?: string;
  details?: string;
  timestamp: string;
}

export interface IncidentNote {
  id: string;
  incidentId: string;
  author: string;
  content: string;
  createdAt: string;
}

export interface Incident {
  id: string;
  incidentNumber: string;
  alertId?: string;
  title: string;
  description: string;
  severity: Severity;
  status: IncidentStatus;
  assignedAnalyst?: string;
  riskScore: number;
  aiConfidence: number;
  affectedUser?: string;
  affectedIp?: string;
  affectedDevice?: string;
  evidenceJson?: string;
  createdAt: string;
  updatedAt: string;
  resolvedAt?: string;
  timeline?: IncidentTimelineEvent[];
  actions?: IncidentAction[];
  notes?: IncidentNote[];
}

export interface ThreatHuntingFilter {
  username?: string;
  email?: string;
  ipAddress?: string;
  device?: string;
  eventType?: string;
  minRiskScore?: number;
  maxRiskScore?: number;
  startDate?: string;
  endDate?: string;
}

export interface ThreatHuntingResult {
  id: string;
  incidentNumber?: string;
  title: string;
  eventType: string;
  source: string;
  user?: string;
  ipAddress?: string;
  device?: string;
  riskScore: number;
  severity: Severity;
  timestamp: string;
  incidentId?: string;
}
