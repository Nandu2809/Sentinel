import { Component, input } from '@angular/core';
import { DatePipe } from '@angular/common';
import { Threat } from '../../../core/models/security.model';
import { SeverityBadgeComponent } from '../severity-badge/severity-badge.component';

@Component({
  selector: 'stn-threat-timeline',
  standalone: true,
  imports: [DatePipe, SeverityBadgeComponent],
  template: `
    <div class="bracket p-4">
      <div class="mono-label mb-4">Attack Timeline</div>
      <div class="relative pl-4 border-l border-line space-y-5">
        @for (t of threats(); track t.id) {
          <div class="relative">
            <span class="absolute -left-[21px] top-1 w-2.5 h-2.5 rounded-full bg-void-900 border-2"
                  [class]="t.severity === 'CRITICAL' ? 'border-signal-critical' : t.severity === 'HIGH' ? 'border-signal-warn' : 'border-signal-live'"></span>
            <div class="flex items-center justify-between gap-3">
              <span class="text-sm font-medium text-ink">{{ t.category }}</span>
              <stn-severity-badge [severity]="t.severity" />
            </div>
            <div class="mt-1 text-xs text-ink-muted">{{ t.pattern }}</div>
            <div class="mt-1 font-mono text-[10px] text-ink-dim">
              Last seen {{ t.lastSeen | date: 'MMM d, HH:mm' }} · {{ t.occurrences }} occurrences · {{ t.confidence }}% AI confidence
            </div>
          </div>
        }
      </div>
    </div>
  `,
})
export class ThreatTimelineComponent {
  threats = input.required<Threat[]>();
}
