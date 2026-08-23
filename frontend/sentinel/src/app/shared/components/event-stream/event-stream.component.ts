import { Component, input } from '@angular/core';
import { DatePipe } from '@angular/common';
import { SecurityEvent } from '../../../core/models/security.model';
import { SeverityBadgeComponent } from '../severity-badge/severity-badge.component';

@Component({
  selector: 'stn-event-stream',
  standalone: true,
  imports: [DatePipe, SeverityBadgeComponent],
  template: `
    <div class="bracket flex flex-col h-full overflow-hidden">
      <div class="flex items-center justify-between px-4 py-3 divider-hairline border-b">
        <span class="mono-label">Live Security Event Stream</span>
        <span class="flex items-center gap-1.5">
          <span class="status-dot bg-signal-live animate-heartbeat"></span>
          <span class="font-mono text-[10px] text-signal-live">STREAMING</span>
        </span>
      </div>
      <div class="flex-1 overflow-y-auto divide-y divide-line-soft">
        @for (evt of events(); track evt.id) {
          <div class="px-4 py-2.5 hover:bg-void-700/40 transition-colors">
            <div class="flex items-center justify-between gap-3">
              <span class="font-mono text-[11px] text-ink-dim">{{ evt.timestamp | date: 'HH:mm:ss' }}</span>
              <stn-severity-badge [severity]="evt.severity" />
            </div>
            <div class="mt-1 font-mono text-xs font-semibold text-ink">{{ evt.type }}</div>
            <div class="mt-1 flex flex-wrap gap-x-4 gap-y-0.5 text-[11px] text-ink-muted">
              <span>User: <span class="text-ink-dim">{{ evt.actor }}</span></span>
              <span>Location: <span class="text-ink-dim">{{ evt.location }}</span></span>
              <span>Risk: <span class="text-ink-dim">{{ evt.riskScore }}</span></span>
            </div>
          </div>
        }
      </div>
    </div>
  `,
})
export class EventStreamComponent {
  events = input.required<SecurityEvent[]>();
}
