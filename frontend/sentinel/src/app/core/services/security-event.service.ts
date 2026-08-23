import { HttpClient } from '@angular/common/http';
import { Injectable, OnDestroy, inject, signal } from '@angular/core';
import { Subject, catchError, interval, map, of } from 'rxjs';
import { environment } from '../../../environments/environment';
import { SecurityEvent, Severity } from '../models/security.model';
import { generateEvent, generateEvents } from './mock-data';

@Injectable({ providedIn: 'root' })
export class SecurityEventService implements OnDestroy {
  private readonly http = inject(HttpClient);
  private readonly _events = signal<SecurityEvent[]>(generateEvents(24));
  readonly events = this._events.asReadonly();

  private readonly newEvent$ = new Subject<SecurityEvent>();
  readonly onNewEvent = this.newEvent$.asObservable();

  private sub = interval(3000).subscribe(() => {
    const evt = generateEvent();
    this._events.update((list) => [evt, ...list].slice(0, 60));
    this.newEvent$.next(evt);
  });

  constructor() {
    this.fetchSecurityEvents();
  }

  fetchSecurityEvents(): void {
    this.http.get<any>(`${environment.apiBaseUrl}/security-events`).pipe(
      map((res) => {
        const rawList = Array.isArray(res) ? res : res?.data || res?.content || [];
        if (!Array.isArray(rawList) || rawList.length === 0) {
          return generateEvents(24);
        }
        return rawList.map((e: any) => ({
          id: e.id || e.eventId || crypto.randomUUID(),
          timestamp: e.timestamp || e.createdAt || new Date().toISOString(),
          type: e.eventType || e.type || 'LOGIN_SUCCEEDED',
          actor: e.email || e.actor || e.userId || 'system',
          location: e.ipAddress || e.location || '172.18.0.1',
          riskScore: Math.round(e.riskScore || 20),
          severity: (e.severity || 'LOW').toUpperCase() as Severity,
          description: e.message || e.description
        }));
      }),
      catchError(() => of(generateEvents(24)))
    ).subscribe((list) => {
      this._events.set(list);
    });
  }

  connect(): void {
    // Integration point for WebSocket / SSE streaming via API Gateway
  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }
}
