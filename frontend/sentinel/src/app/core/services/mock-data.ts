import {
  SecurityAlert,
  SecurityEvent,
  Severity,
  SystemHealthMetric,
  Threat,
  UserRiskProfile,
} from '../models/security.model';

const EVENT_TYPES = [
  'LOGIN_FAILED',
  'LOGIN_SUCCESS',
  'BRUTE_FORCE_DETECTED',
  'IMPOSSIBLE_TRAVEL',
  'PRIVILEGE_ESCALATION',
  'NEW_DEVICE_LOGIN',
  'DATA_EXFIL_ATTEMPT',
  'MFA_BYPASS_ATTEMPT',
  'SESSION_HIJACK_SUSPECTED',
];

const LOCATIONS = ['Bengaluru, IN', 'Unknown', 'Frankfurt, DE', 'Ashburn, US', 'Singapore, SG', 'Tor Exit Node'];

function pick<T>(arr: T[]): T {
  return arr[Math.floor(Math.random() * arr.length)];
}

function severityFromScore(score: number): Severity {
  if (score >= 90) return 'CRITICAL';
  if (score >= 70) return 'HIGH';
  if (score >= 40) return 'MEDIUM';
  if (score >= 15) return 'LOW';
  return 'SAFE';
}

export function generateEvent(): SecurityEvent {
  const riskScore = Math.floor(Math.random() * 100);
  return {
    id: crypto.randomUUID(),
    timestamp: new Date().toISOString(),
    type: pick(EVENT_TYPES),
    actor: pick(['admin@sentinel.io', 'n.kishore@sentinel.io', 'svc-billing@sentinel.io', 'r.patel@sentinel.io']),
    location: pick(LOCATIONS),
    riskScore,
    severity: severityFromScore(riskScore),
  };
}

export function generateEvents(count: number): SecurityEvent[] {
  return Array.from({ length: count }, generateEvent).sort(
    (a, b) => new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime(),
  );
}

export function generateThreats(): Threat[] {
  const categories = [
    'Credential Attack',
    'Lateral Movement',
    'Data Exfiltration',
    'Privilege Escalation',
    'Anomalous Access Pattern',
    'Command & Control Beacon',
  ];
  return categories.map((category, i) => {
    const confidence = 60 + Math.floor(Math.random() * 39);
    const score = 30 + Math.floor(Math.random() * 69);
    return {
      id: `THR-${1000 + i}`,
      category,
      confidence,
      severity: severityFromScore(score),
      firstSeen: new Date(Date.now() - (i + 1) * 3600_000 * 7).toISOString(),
      lastSeen: new Date(Date.now() - i * 3600_000).toISOString(),
      pattern: pick(['Repeated auth failure burst', 'Off-hours access spike', 'Unusual data volume', 'New geo + new device combo']),
      occurrences: 4 + Math.floor(Math.random() * 40),
    };
  });
}

export function generateAlerts(): SecurityAlert[] {
  const threats = ['Credential Attack', 'Lateral Movement', 'Data Exfiltration', 'Insider Anomaly', 'C2 Beaconing'];
  const statuses: SecurityAlert['status'][] = ['NEW', 'INVESTIGATING', 'ACKNOWLEDGED', 'RESOLVED'];
  return Array.from({ length: 12 }, (_, i) => {
    const score = 35 + Math.floor(Math.random() * 64);
    return {
      id: crypto.randomUUID(),
      incidentNumber: `#${48200 + i}`,
      threat: pick(threats),
      severity: severityFromScore(score),
      riskScore: score,
      status: pick(statuses),
      assignedTo: Math.random() > 0.4 ? pick(['A. Rao', 'S. Bilal', 'V. Jaya Krishna', 'Unassigned']) : undefined,
      createdAt: new Date(Date.now() - i * 3600_000 * 3).toISOString(),
      description: 'Correlated signal from Threat Detection Engine flagged for analyst review.',
    };
  });
}

export function generateRiskProfile(name = 'Nanda Kishore'): UserRiskProfile {
  return {
    userId: 'usr-2460402',
    name,
    score: 82,
    trend: [41, 46, 52, 58, 63, 71, 76, 82],
    factors: [
      { label: 'Multiple failed logins', weight: 28, direction: 'up' },
      { label: 'New device', weight: 21, direction: 'up' },
      { label: 'Suspicious location', weight: 24, direction: 'up' },
      { label: 'MFA enabled', weight: 9, direction: 'down' },
      { label: 'Consistent working hours', weight: 6, direction: 'down' },
    ],
  };
}

export function generateHealth(): SystemHealthMetric[] {
  return [
    { label: 'Kafka Event Stream', value: 99.98, status: 'SAFE' },
    { label: 'Threat Detection Engine', value: 99.7, status: 'SAFE' },
    { label: 'Risk Intelligence Engine', value: 97.2, status: 'LOW' },
    { label: 'Alert Management', value: 100, status: 'SAFE' },
  ];
}
