import { Injectable, OnDestroy, signal } from '@angular/core';
import { Subject, interval } from 'rxjs';
import { SecurityEvent } from '../models/security.model';
import { generateEvent, generateEvents } from './mock-data';

/**
 * Bridges the frontend to the Security Event Streaming layer (Kafka -> Monitoring Service).
 * Currently simulates the feed locally; swap `connect()` for a WebSocket client against
 * /ws/security-events once the gateway is available, keeping the same Observable contract.
 */
@Injectable({ providedIn: 'root' })
export class SecurityEventService implements OnDestroy {
  private readonly wsEndpoint = '/ws/security-events';
  private readonly _events = signal<SecurityEvent[]>(generateEvents(24));
  readonly events = this._events.asReadonly();

  private readonly newEvent$ = new Subject<SecurityEvent>();
  readonly onNewEvent = this.newEvent$.asObservable();

  private sub = interval(2600).subscribe(() => {
    const evt = generateEvent();
    this._events.update((list) => [evt, ...list].slice(0, 60));
    this.newEvent$.next(evt);
  });

  connect(): void {
    // Reserved: new WebSocket(this.wsEndpoint) integration point.
  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }
}
