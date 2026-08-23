import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, catchError, map, of } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Severity, Threat } from '../models/security.model';
import { generateThreats } from './mock-data';

@Injectable({ providedIn: 'root' })
export class ThreatService {
  private readonly http = inject(HttpClient);

  /** GET /api/v1/threats — Threat Detection Engine output */
  getThreats(): Observable<Threat[]> {
    return this.http.get<any>(`${environment.apiBaseUrl}/threats`).pipe(
      map((res) => {
        const rawList = Array.isArray(res) ? res : res?.data || res?.content || [];
        if (!Array.isArray(rawList) || rawList.length === 0) {
          return generateThreats();
        }
        return rawList.map((t: any) => ({
          id: t.id || t.threatId || crypto.randomUUID(),
          category: t.category || t.threatType || 'BRUTE_FORCE_ATTACK',
          confidence: t.confidence ?? Math.round(t.riskScore || 85),
          severity: (t.severity || 'HIGH').toUpperCase() as Severity,
          firstSeen: t.firstSeen || t.createdAt || new Date().toISOString(),
          lastSeen: t.lastSeen || t.updatedAt || new Date().toISOString(),
          pattern: t.pattern || t.description || 'Sliding window threshold exceeded',
          occurrences: t.occurrences ?? 5,
        }));
      }),
      catchError(() => of(generateThreats()))
    );
  }
}
