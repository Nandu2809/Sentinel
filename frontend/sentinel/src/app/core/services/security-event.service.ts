import { HttpClient } from '@angular/common/http';
import { Injectable, OnDestroy, inject, signal } from '@angular/core';
import { Subject, catchError, map, of } from 'rxjs';
import { environment } from '../../../environments/environment';
import { SecurityEvent, Severity } from '../models/security.model';
import { generateEvents } from './mock-data';

@Injectable({ providedIn: 'root' })
export class SecurityEventService implements OnDestroy {
  private readonly http = inject(HttpClient);
  private readonly _events = signal<SecurityEvent[]>(generateEvents(10));
  readonly events = this._events.asReadonly();

  private readonly newEvent$ = new Subject<SecurityEvent>();
  readonly onNewEvent = this.newEvent$.asObservable();

  private socket?: WebSocket;
  private reconnectTimer?: any;

  constructor() {
    this.fetchSecurityEvents();
    this.connect();
  }

  fetchSecurityEvents(): void {
    this.http.get<any>(`${environment.apiBaseUrl}/security-events`).pipe(
      map((res) => {
        const rawList = Array.isArray(res) ? res : res?.data || res?.content || [];
        if (!Array.isArray(rawList) || rawList.length === 0) {
          return generateEvents(10);
        }
        return rawList.map((e: any) => this.mapEnvelopeToEvent(e));
      }),
      catchError(() => of(generateEvents(10)))
    ).subscribe((list) => {
      this._events.set(list);
    });
  }

  connect(): void {
    if (this.socket && (this.socket.readyState === WebSocket.OPEN || this.socket.readyState === WebSocket.CONNECTING)) {
      return;
    }

    const wsUrl = environment.production
      ? `ws://${window.location.host}/ws/security-events`
      : 'ws://localhost:8082/ws/security-events';

    try {
      this.socket = new WebSocket(wsUrl);

      this.socket.onopen = () => {
        console.log('[SecurityEventService] Real-time WebSocket connected:', wsUrl);
      };

      this.socket.onmessage = (messageEvent) => {
        try {
          const envelope = JSON.parse(messageEvent.data);
          const securityEvent = this.mapEnvelopeToEvent(envelope);

          this._events.update((list) => [securityEvent, ...list].slice(0, 60));
          this.newEvent$.next(securityEvent);
        } catch (err) {
          console.error('[SecurityEventService] Error parsing WebSocket event payload:', err);
        }
      };

      this.socket.onerror = (err) => {
        console.warn('[SecurityEventService] WebSocket error:', err);
      };

      this.socket.onclose = () => {
        console.log('[SecurityEventService] WebSocket connection closed, scheduling reconnect...');
        this.scheduleReconnect();
      };
    } catch (e) {
      console.warn('[SecurityEventService] Failed to initialize WebSocket client:', e);
      this.scheduleReconnect();
    }
  }

  private scheduleReconnect(): void {
    if (this.reconnectTimer) {
      clearTimeout(this.reconnectTimer);
    }
    this.reconnectTimer = setTimeout(() => this.connect(), 5000);
  }

  private mapEnvelopeToEvent(envelope: any): SecurityEvent {
    const isFailed = envelope.outcome === 'FAILED' || (envelope.eventType && envelope.eventType.includes('FAILED'));
    const isSuccess = envelope.outcome === 'SUCCESS' || (envelope.eventType && envelope.eventType.includes('SUCCESS'));

    let severity: Severity = 'LOW';
    if (isFailed) {
      severity = 'HIGH';
    } else if (isSuccess) {
      severity = 'SAFE';
    }

    const defaultScore = isFailed ? 75 : 15;

    return {
      id: envelope.eventId || envelope.id || crypto.randomUUID(),
      timestamp: envelope.timestamp || envelope.createdAt || new Date().toISOString(),
      type: envelope.eventType || envelope.type || 'SECURITY_EVENT',
      actor: envelope.email || envelope.username || envelope.userId || envelope.actor || 'system',
      location: envelope.ipAddress || envelope.location || '172.18.0.1',
      riskScore: Math.round(envelope.riskScore ?? defaultScore),
      severity,
      description: envelope.message || envelope.description || `${envelope.eventType || 'Event'} (${envelope.outcome || 'PROCESSED'})`
    };
  }

  ngOnDestroy(): void {
    if (this.reconnectTimer) {
      clearTimeout(this.reconnectTimer);
    }
    if (this.socket) {
      this.socket.close();
    }
  }
}
