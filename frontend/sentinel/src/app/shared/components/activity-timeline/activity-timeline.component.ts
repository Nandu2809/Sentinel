import { Component, input } from '@angular/core';
import { DatePipe } from '@angular/common';

export interface ActivityItem {
  timestamp: string;
  label: string;
  detail: string;
}

@Component({
  selector: 'stn-activity-timeline',
  standalone: true,
  imports: [DatePipe],
  template: `
    <div class="bracket p-4">
      <div class="mono-label mb-4">{{ title() }}</div>
      <div class="space-y-3">
        @for (a of items(); track a.timestamp + a.label) {
          <div class="flex items-start gap-3 divider-hairline pb-3 border-b last:border-0 last:pb-0">
            <span class="font-mono text-[10px] text-ink-dim w-24 shrink-0">{{ a.timestamp | date: 'MMM d, HH:mm' }}</span>
            <div>
              <div class="text-xs font-medium text-ink">{{ a.label }}</div>
              <div class="text-[11px] text-ink-muted">{{ a.detail }}</div>
            </div>
          </div>
        }
      </div>
    </div>
  `,
})
export class ActivityTimelineComponent {
  title = input<string>('Activity Timeline');
  items = input.required<ActivityItem[]>();
}
