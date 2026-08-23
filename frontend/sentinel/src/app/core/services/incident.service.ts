import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { Observable, catchError, map, of, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  Incident,
  IncidentActionType,
  IncidentNote,
  IncidentStatus,
  ThreatHuntingFilter,
  ThreatHuntingResult
} from '../models/incident.model';

@Injectable({ providedIn: 'root' })
export class IncidentService {
  private readonly http = inject(HttpClient);

  private readonly _incidents = signal<Incident[]>(this.generateMockIncidents());
  readonly incidents = this._incidents.asReadonly();

  private readonly _selectedIncident = signal<Incident | null>(null);
  readonly selectedIncident = this._selectedIncident.asReadonly();

  constructor() {
    this.fetchIncidents();
  }

  fetchIncidents(status?: IncidentStatus): void {
    let params = new HttpParams();
    if (status) {
      params = params.set('status', status);
    }

    this.http.get<any>(`${environment.apiBaseUrl}/incidents`, { params }).pipe(
      map((res) => {
        const rawList = res?.data?.content || res?.data || res?.content || (Array.isArray(res) ? res : []);
        if (!Array.isArray(rawList) || rawList.length === 0) {
          return this.generateMockIncidents();
        }
        return rawList.map(this.mapIncident);
      }),
      catchError(() => of(this.generateMockIncidents()))
    ).subscribe((list) => {
      this._incidents.set(list);
    });
  }

  fetchIncidentById(id: string): Observable<Incident> {
    return this.http.get<any>(`${environment.apiBaseUrl}/incidents/${id}`).pipe(
      map((res) => {
        const raw = res?.data || res;
        return this.mapIncident(raw);
      }),
      tap((inc) => this._selectedIncident.set(inc)),
      catchError(() => {
        const found = this._incidents().find((i) => i.id === id) || this.generateMockIncidents()[0];
        this._selectedIncident.set(found);
        return of(found);
      })
    );
  }

  assignAnalyst(id: string, assignedAnalyst: string): Observable<any> {
    return this.http.post<any>(`${environment.apiBaseUrl}/incidents/${id}/assign`, { assignedAnalyst, assignedBy: 'SOC_LEAD' }).pipe(
      tap(() => this.fetchIncidentById(id).subscribe()),
      catchError(() => {
        this._incidents.update((list) =>
          list.map((i) => (i.id === id ? { ...i, assignedAnalyst, status: 'ACKNOWLEDGED' } : i))
        );
        const sel = this._selectedIncident();
        if (sel && sel.id === id) {
          this._selectedIncident.set({ ...sel, assignedAnalyst, status: 'ACKNOWLEDGED' });
        }
        return of(null);
      })
    );
  }

  updateStatus(id: string, status: IncidentStatus, notes?: string): Observable<any> {
    return this.http.put<any>(`${environment.apiBaseUrl}/incidents/${id}/status`, { status, notes, updatedBy: 'SOC_ANALYST' }).pipe(
      tap(() => this.fetchIncidentById(id).subscribe()),
      catchError(() => {
        this._incidents.update((list) =>
          list.map((i) => (i.id === id ? { ...i, status } : i))
        );
        const sel = this._selectedIncident();
        if (sel && sel.id === id) {
          this._selectedIncident.set({ ...sel, status });
        }
        return of(null);
      })
    );
  }

  addNote(id: string, content: string): Observable<IncidentNote> {
    return this.http.post<any>(`${environment.apiBaseUrl}/incidents/${id}/notes`, { content, author: 'SOC_ANALYST' }).pipe(
      map((res) => res?.data || res),
      tap(() => this.fetchIncidentById(id).subscribe()),
      catchError(() => {
        const newNote: IncidentNote = {
          id: crypto.randomUUID(),
          incidentId: id,
          author: 'SOC_ANALYST',
          content,
          createdAt: new Date().toISOString()
        };
        const sel = this._selectedIncident();
        if (sel && sel.id === id) {
          this._selectedIncident.set({
            ...sel,
            notes: [newNote, ...(sel.notes || [])]
          });
        }
        return of(newNote);
      })
    );
  }

  performAction(id: string, actionType: IncidentActionType, target?: string, details?: string): Observable<any> {
    return this.http.post<any>(`${environment.apiBaseUrl}/incidents/${id}/actions`, {
      actionType,
      performedBy: 'SOC_ANALYST',
      target,
      details
    }).pipe(
      tap(() => this.fetchIncidentById(id).subscribe()),
      catchError(() => {
        const sel = this._selectedIncident();
        if (sel && sel.id === id) {
          const newStatus = actionType === 'RESOLVE' ? 'RESOLVED' : (actionType === 'ACKNOWLEDGE' ? 'ACKNOWLEDGED' : sel.status);
          this._selectedIncident.set({
            ...sel,
            status: newStatus,
            actions: [
              {
                id: crypto.randomUUID(),
                incidentId: id,
                actionType,
                performedBy: 'SOC_ANALYST',
                target: target || sel.affectedIp || sel.affectedUser,
                details: details || `Action executed: ${actionType}`,
                timestamp: new Date().toISOString()
              },
              ...(sel.actions || [])
            ]
          });
        }
        return of(null);
      })
    );
  }

  searchThreats(filter: ThreatHuntingFilter): Observable<ThreatHuntingResult[]> {
    let params = new HttpParams();
    if (filter.username) params = params.set('username', filter.username);
    if (filter.ipAddress) params = params.set('ipAddress', filter.ipAddress);
    if (filter.device) params = params.set('device', filter.device);
    if (filter.eventType) params = params.set('eventType', filter.eventType);
    if (filter.minRiskScore) params = params.set('minRiskScore', filter.minRiskScore.toString());

    return this.http.get<any>(`${environment.apiBaseUrl}/threat-hunting/search`, { params }).pipe(
      map((res) => res?.data || res || []),
      catchError(() => of(this.generateMockThreatResults()))
    );
  }

  triggerTestScenario(): Observable<any> {
    return this.http.post<any>(`${environment.apiBaseUrl}/incidents/test/trigger-impossible-travel`, {}).pipe(
      tap(() => this.fetchIncidents()),
      catchError(() => of(null))
    );
  }

  private mapIncident(a: any): Incident {
    return {
      id: a.id || crypto.randomUUID(),
      incidentNumber: a.incidentNumber || 'INC-2026-0001',
      alertId: a.alertId,
      title: a.title || 'Security Incident Detected',
      description: a.description || 'Behavioral anomaly triggered critical security threshold.',
      severity: a.severity || 'HIGH',
      status: a.status || 'OPEN',
      assignedAnalyst: a.assignedAnalyst,
      riskScore: a.riskScore || 88,
      aiConfidence: a.aiConfidence || 92.5,
      affectedUser: a.affectedUser || 'victim_user',
      affectedIp: a.affectedIp || '192.168.1.100',
      affectedDevice: a.affectedDevice || 'Linux x86_64 / Terminal',
      evidenceJson: a.evidenceJson,
      createdAt: a.createdAt || new Date().toISOString(),
      updatedAt: a.updatedAt || new Date().toISOString(),
      resolvedAt: a.resolvedAt,
      timeline: a.timeline || [],
      actions: a.actions || [],
      notes: a.notes || []
    };
  }

  private generateMockIncidents(): Incident[] {
    const now = new Date();
    return [
      {
        id: 'inc-001',
        incidentNumber: 'INC-2026-0001',
        alertId: 'alt-001',
        title: 'CRITICAL: Impossible Travel Attack Detected',
        description: 'User authenticated from New York, then 5 minutes later from Tokyo. AI Anomaly confidence 98.5%.',
        severity: 'CRITICAL',
        status: 'OPEN',
        assignedAnalyst: undefined,
        riskScore: 98,
        aiConfidence: 98.5,
        affectedUser: 'john_doe@sentinel.sec',
        affectedIp: '203.0.113.195',
        affectedDevice: 'MacBookPro18,1 / Safari 17.4',
        evidenceJson: '{"login1":"NYC","login2":"TYO","deltaMinutes":5}',
        createdAt: new Date(now.getTime() - 15 * 60000).toISOString(),
        updatedAt: new Date(now.getTime() - 15 * 60000).toISOString(),
        timeline: [
          {
            id: 't-1',
            incidentId: 'inc-001',
            eventType: 'LOGIN_FAILED',
            source: 'Auth Service',
            summary: 'Multiple failed password attempts for user john_doe@sentinel.sec',
            timestamp: new Date(now.getTime() - 20 * 60000).toISOString()
          },
          {
            id: 't-2',
            incidentId: 'inc-001',
            eventType: 'BRUTE_FORCE_DETECTED',
            source: 'Threat Engine',
            summary: 'Brute force pattern matched (12 attempts in 30s)',
            timestamp: new Date(now.getTime() - 18 * 60000).toISOString()
          },
          {
            id: 't-3',
            incidentId: 'inc-001',
            eventType: 'AI_ANOMALY',
            source: 'AI Behavioral Engine',
            summary: 'Impossible travel anomaly detected with 98.5% confidence',
            timestamp: new Date(now.getTime() - 16 * 60000).toISOString()
          },
          {
            id: 't-4',
            incidentId: 'inc-001',
            eventType: 'RISK_CRITICAL',
            source: 'Risk Engine',
            summary: 'Calculated risk score 98/100 exceeded threshold',
            timestamp: new Date(now.getTime() - 15 * 60000).toISOString()
          },
          {
            id: 't-5',
            incidentId: 'inc-001',
            eventType: 'ALERT_CREATED',
            source: 'Alert Engine',
            summary: 'Alert ALT-8921 created and escalated to SOC Command Center',
            timestamp: new Date(now.getTime() - 15 * 60000).toISOString()
          }
        ],
        actions: [],
        notes: [
          {
            id: 'n-1',
            incidentId: 'inc-001',
            author: 'SYSTEM',
            content: 'Incident automatically created from CRITICAL risk event.',
            createdAt: new Date(now.getTime() - 15 * 60000).toISOString()
          }
        ]
      },
      {
        id: 'inc-002',
        incidentNumber: 'INC-2026-0002',
        alertId: 'alt-002',
        title: 'HIGH: SQL Injection Exploit Attempt',
        description: 'Malicious SQL payload detected in authorization header query parameter.',
        severity: 'HIGH',
        status: 'INVESTIGATING',
        assignedAnalyst: 'sarah_connor',
        riskScore: 85,
        aiConfidence: 89.0,
        affectedUser: 'api_gateway_svc',
        affectedIp: '198.51.100.42',
        affectedDevice: 'curl/7.68.0',
        evidenceJson: '{"query":"SELECT * FROM users WHERE 1=1--"}',
        createdAt: new Date(now.getTime() - 45 * 60000).toISOString(),
        updatedAt: new Date(now.getTime() - 25 * 60000).toISOString(),
        timeline: [
          {
            id: 't-201',
            incidentId: 'inc-002',
            eventType: 'SQL_INJECTION_DETECTED',
            source: 'Threat Engine',
            summary: 'Pattern UNION SELECT matched in HTTP request body',
            timestamp: new Date(now.getTime() - 45 * 60000).toISOString()
          }
        ],
        actions: [
          {
            id: 'a-1',
            incidentId: 'inc-002',
            actionType: 'ASSIGN',
            performedBy: 'SOC_LEAD',
            target: 'sarah_connor',
            details: 'Assigned to Senior SOC Analyst Sarah',
            timestamp: new Date(now.getTime() - 30 * 60000).toISOString()
          }
        ],
        notes: [
          {
            id: 'n-2',
            incidentId: 'inc-002',
            author: 'sarah_connor',
            content: 'Analyzing ingress WAF rule block logs.',
            createdAt: new Date(now.getTime() - 25 * 60000).toISOString()
          }
        ]
      }
    ];
  }

  private generateMockThreatResults(): ThreatHuntingResult[] {
    return [
      {
        id: 'th-1',
        incidentNumber: 'INC-2026-0001',
        title: 'Impossible Travel Anomaly',
        eventType: 'IMPOSSIBLE_TRAVEL_ATTACK',
        source: 'AI Engine',
        user: 'john_doe@sentinel.sec',
        ipAddress: '203.0.113.195',
        device: 'MacBookPro / Safari',
        riskScore: 98,
        severity: 'CRITICAL',
        timestamp: new Date().toISOString(),
        incidentId: 'inc-001'
      },
      {
        id: 'th-2',
        incidentNumber: 'INC-2026-0002',
        title: 'SQL Injection Signature Match',
        eventType: 'SQL_INJECTION',
        source: 'Threat Engine',
        user: 'api_gateway_svc',
        ipAddress: '198.51.100.42',
        device: 'curl/7.68.0',
        riskScore: 85,
        severity: 'HIGH',
        timestamp: new Date(Date.now() - 3600000).toISOString(),
        incidentId: 'inc-002'
      }
    ];
  }
}
