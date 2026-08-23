import { Component, EventEmitter, Output, input } from '@angular/core';
import { DatePipe } from '@angular/common';
import { SecurityAlert } from '../../../core/models/security.model';
import { SeverityBadgeComponent } from '../severity-badge/severity-badge.component';

const STATUS_STYLE: Record<SecurityAlert['status'], string> = {
  NEW: 'text-signal-live',
  INVESTIGATING: 'text-signal-warn',
  ACKNOWLEDGED: 'text-signal-intel',
  RESOLVED: 'text-signal-safe',
};

@Component({
  selector: 'stn-alert-panel',
  standalone: true,
  imports: [DatePipe, SeverityBadgeComponent],
  template: `
    <div class="bracket p-4 flex flex-col gap-3">
      <div class="flex items-start justify-between">
        <div>
          <div class="font-mono text-[11px] text-ink-dim">INCIDENT {{ alert().incidentNumber }}</div>
          <div class="text-sm font-semibold text-ink mt-0.5">{{ alert().threat }}</div>
        </div>
        <stn-severity-badge [severity]="alert().severity" />
      </div>

      <div class="grid grid-cols-3 gap-2 divider-hairline pt-3 border-t">
        <div>
          <div class="mono-label">Risk Score</div>
          <div class="font-mono text-lg font-semibold text-ink">{{ alert().riskScore }}</div>
        </div>
        <div>
          <div class="mono-label">Status</div>
          <div class="font-mono text-xs font-semibold mt-1.5" [class]="statusClass()">{{ alert().status }}</div>
        </div>
        <div>
          <div class="mono-label">Analyst</div>
          <div class="text-xs text-ink-muted mt-1.5">{{ alert().assignedTo || 'Unassigned' }}</div>
        </div>
      </div>

      <p class="text-xs text-ink-muted">{{ alert().description }}</p>
      <div class="font-mono text-[10px] text-ink-dim">Opened {{ alert().createdAt | date: 'MMM d, HH:mm' }}</div>

      <div class="flex gap-2 pt-1">
        <button
          class="flex-1 bg-void-700 hover:bg-void-600 text-ink text-xs font-medium py-1.5 rounded-sm transition-colors"
          (click)="acknowledge.emit(alert().id)"
        >
          Acknowledge
        </button>
        <button
          class="flex-1 bg-void-700 hover:bg-void-600 text-ink text-xs font-medium py-1.5 rounded-sm transition-colors"
          (click)="investigate.emit(alert().id)"
        >
          Investigate
        </button>
        <button
          class="flex-1 bg-signal-safe/10 hover:bg-signal-safe/20 text-signal-safe text-xs font-medium py-1.5 rounded-sm transition-colors"
          (click)="resolve.emit(alert().id)"
        >
          Resolve
        </button>
      </div>
    </div>
  `,
})
export class AlertPanelComponent {
  alert = input.required<SecurityAlert>();

  @Output() acknowledge = new EventEmitter<string>();
  @Output() investigate = new EventEmitter<string>();
  @Output() resolve = new EventEmitter<string>();

  statusClass(): string {
    return STATUS_STYLE[this.alert().status];
  }
}
