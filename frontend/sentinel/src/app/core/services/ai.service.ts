import { HttpClient } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { catchError, map, of } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface AIIntelligenceFactor {
  name: string;
  impact: string;
  score: number;
}

export interface AIIntelligenceTimelineItem {
  timestamp: string;
  location: string;
  status: string;
}

export interface AIThreatIntelligence {
  user: string;
  behaviorStatus: string;
  aiConfidence: number;
  anomalyScore: number;
  reason: string;
  factors: AIIntelligenceFactor[];
  timeline: AIIntelligenceTimelineItem[];
}

@Injectable({ providedIn: 'root' })
export class AIService {
  private readonly http = inject(HttpClient);

  private readonly _intelligence = signal<AIThreatIntelligence>({
    user: 'nanda.test@sentinel.com',
    behaviorStatus: 'ABNORMAL',
    aiConfidence: 94,
    anomalyScore: 92.5,
    reason: 'Login behavior differs from baseline: Impossible travel detected (India -> USA in 5 minutes)',
    factors: [
      { name: 'Impossible Travel (India -> USA)', impact: 'HIGH', score: 95 },
      { name: 'New Device Fingerprint', impact: 'MEDIUM', score: 45 },
      { name: 'Unusual Login Hour', impact: 'LOW', score: 30 }
    ],
    timeline: [
      { timestamp: '10:00 AM', location: 'Mumbai, IN', status: 'SAFE' },
      { timestamp: '10:05 AM', location: 'New York, US', status: 'CRITICAL_ANOMALY' }
    ]
  });

  readonly intelligence = this._intelligence.asReadonly();

  constructor() {
    this.fetchIntelligence();
  }

  fetchIntelligence(email?: string): void {
    const url = `${environment.apiBaseUrl}/ai/intelligence${email ? '?email=' + encodeURIComponent(email) : ''}`;
    this.http.get<any>(url).pipe(
      map((res) => {
        if (res && res.data) {
          return res.data as AIThreatIntelligence;
        }
        return this._intelligence();
      }),
      catchError(() => of(this._intelligence()))
    ).subscribe((data) => {
      this._intelligence.set(data);
    });
  }
}
