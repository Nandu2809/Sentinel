import { Component, computed, input } from '@angular/core';
import { Severity } from '../../../core/models/security.model';

const STYLES: Record<Severity, { text: string; dot: string; ring: string }> = {
  SAFE: { text: 'text-signal-safe', dot: 'bg-signal-safe', ring: 'ring-signal-safe/30' },
  LOW: { text: 'text-signal-safe', dot: 'bg-signal-safe', ring: 'ring-signal-safe/30' },
  MEDIUM: { text: 'text-signal-warn', dot: 'bg-signal-warn', ring: 'ring-signal-warn/30' },
  HIGH: { text: 'text-signal-warn', dot: 'bg-signal-warn', ring: 'ring-signal-warn/30' },
  CRITICAL: { text: 'text-signal-critical', dot: 'bg-signal-critical', ring: 'ring-signal-critical/30' },
};

@Component({
  selector: 'stn-severity-badge',
  standalone: true,
  template: `
    <span
      class="inline-flex items-center gap-1.5 rounded-sm px-2 py-0.5 font-mono text-[10px] uppercase tracking-wider ring-1"
      [class]="style().text + ' ' + style().ring"
    >
      <span class="status-dot" [class]="style().dot"></span>
      {{ severity() }}
    </span>
  `,
})
export class SeverityBadgeComponent {
  severity = input.required<Severity>();
  style = computed(() => STYLES[this.severity()]);
}
