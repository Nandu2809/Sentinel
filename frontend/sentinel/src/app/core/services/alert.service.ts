import { Injectable, signal } from '@angular/core';
import { AlertStatus, SecurityAlert } from '../models/security.model';
import { generateAlerts } from './mock-data';

@Injectable({ providedIn: 'root' })
export class AlertService {
  private readonly _alerts = signal<SecurityAlert[]>(generateAlerts());
  readonly alerts = this._alerts.asReadonly();

  /** POST /api/v1/alerts/:id/acknowledge */
  acknowledge(id: string): void {
    this.updateStatus(id, 'ACKNOWLEDGED');
  }

  /** POST /api/v1/alerts/:id/assign */
  assign(id: string, analyst: string): void {
    this._alerts.update((list) => list.map((a) => (a.id === id ? { ...a, assignedTo: analyst } : a)));
  }

  /** POST /api/v1/alerts/:id/resolve */
  resolve(id: string): void {
    this.updateStatus(id, 'RESOLVED');
  }

  investigate(id: string): void {
    this.updateStatus(id, 'INVESTIGATING');
  }

  private updateStatus(id: string, status: AlertStatus): void {
    this._alerts.update((list) => list.map((a) => (a.id === id ? { ...a, status } : a)));
  }
}
