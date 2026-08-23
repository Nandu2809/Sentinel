import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, catchError, map, of } from 'rxjs';
import { environment } from '../../../environments/environment';
import { UserRiskProfile } from '../models/security.model';
import { generateRiskProfile } from './mock-data';

@Injectable({ providedIn: 'root' })
export class RiskService {
  private readonly http = inject(HttpClient);

  /** GET /api/v1/risk — Risk Intelligence Engine output */
  getUserRisk(name?: string): Observable<UserRiskProfile> {
    return this.http.get<any>(`${environment.apiBaseUrl}/risk`).pipe(
      map((res) => {
        const data = res?.data || res;
        if (!data || (typeof data === 'object' && Object.keys(data).length === 0)) {
          return generateRiskProfile(name);
        }
        return {
          userId: data.userId || data.id || 'usr-2460402',
          name: name || data.name || 'Nanda Kishore',
          score: data.riskScore ?? data.score ?? 85,
          trend: Array.isArray(data.trend) ? data.trend : [45, 52, 60, 75, 85],
          factors: Array.isArray(data.factors) ? data.factors : [
            { label: 'Brute Force Attempt', weight: 50, direction: 'up' },
            { label: 'Unusual IP Location', weight: 30, direction: 'up' },
            { label: 'MFA Verified', weight: -15, direction: 'down' }
          ]
        };
      }),
      catchError(() => of(generateRiskProfile(name)))
    );
  }
}
