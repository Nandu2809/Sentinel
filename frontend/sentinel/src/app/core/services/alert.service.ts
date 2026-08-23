import { HttpClient } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { catchError, map, of } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AlertStatus, SecurityAlert, Severity } from '../models/security.model';
import { generateAlerts } from './mock-data';

@Injectable({ providedIn: 'root' })
export class AlertService {
  private readonly http = inject(HttpClient);
  private readonly _alerts = signal<SecurityAlert[]>(generateAlerts());
  readonly alerts = this._alerts.asReadonly();

  constructor() {
    this.fetchAlerts();
  }

  /** GET /api/v1/alerts — Alert Management Engine output */
  fetchAlerts(): void {
    this.http.get<any>(`${environment.apiBaseUrl}/alerts`).pipe(
      map((res) => {
        const rawList = Array.isArray(res) ? res : res?.data || res?.content || [];
        if (!Array.isArray(rawList) || rawList.length === 0) {
          return generateAlerts();
        }
        return rawList.map((a: any) => ({
          id: a.id || crypto.randomUUID(),
          incidentNumber: a.alertCode || a.incidentNumber || 'ALT-' + (a.id || '').substring(0, 8),
          threat: a.alertType || a.threatType || a.title || 'BRUTE_FORCE_ATTACK',
          severity: (a.severity || 'HIGH').toUpperCase() as Severity,
          riskScore: Math.round(a.riskScore || 85),
          status: (a.status || 'OPEN') as AlertStatus,
          assignedTo: a.assignedAnalyst || a.assignedTo,
          createdAt: a.createdAt || new Date().toISOString(),
          description: a.message || a.description || 'Security Incident Detected'
        }));
      }),
      catchError(() => of(generateAlerts()))
    ).subscribe((list) => {
      this._alerts.set(list);
    });
  }

  /** POST /api/v1/alerts/:id/acknowledge */
  acknowledge(id: string): void {
    this.http.post(`${environment.apiBaseUrl}/alerts/${id}/acknowledge`, {}).pipe(
      catchError(() => of(null))
    ).subscribe();
    this.updateStatus(id, 'ACKNOWLEDGED');
  }

  /** POST /api/v1/alerts/:id/assign */
  assign(id: string, analyst: string): void {
    this.http.post(`${environment.apiBaseUrl}/alerts/${id}/assign`, { analyst }).pipe(
      catchError(() => of(null))
    ).subscribe();
    this._alerts.update((list) => list.map((a) => (a.id === id ? { ...a, assignedTo: analyst } : a)));
  }

  /** POST /api/v1/alerts/:id/resolve */
  resolve(id: string): void {
    this.http.post(`${environment.apiBaseUrl}/alerts/${id}/resolve`, {}).pipe(
      catchError(() => of(null))
    ).subscribe();
    this.updateStatus(id, 'RESOLVED');
  }

  investigate(id: string): void {
    this.http.post(`${environment.apiBaseUrl}/alerts/${id}/investigate`, {}).pipe(
      catchError(() => of(null))
    ).subscribe();
    this.updateStatus(id, 'INVESTIGATING');
  }

  private updateStatus(id: string, status: AlertStatus): void {
    this._alerts.update((list) => list.map((a) => (a.id === id ? { ...a, status } : a)));
  }
}
