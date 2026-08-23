import { Component, input } from '@angular/core';

export type MetricTone = 'safe' | 'warn' | 'critical' | 'intel' | 'live';

const TONE_TEXT: Record<MetricTone, string> = {
  safe: 'text-signal-safe',
  warn: 'text-signal-warn',
  critical: 'text-signal-critical',
  intel: 'text-signal-intel',
  live: 'text-signal-live',
};

@Component({
  selector: 'stn-security-metric-card',
  standalone: true,
  template: `
    <div class="bracket p-4 flex flex-col gap-3 min-h-[112px]">
      <div class="flex items-start justify-between">
        <span class="mono-label">{{ label() }}</span>
        @if (trendLabel()) {
          <span class="font-mono text-[10px]" [class]="toneClass()">{{ trendLabel() }}</span>
        }
      </div>
      <div class="flex items-baseline gap-2">
        <span class="font-mono text-3xl font-semibold" [class]="toneClass()">{{ value() }}</span>
        @if (unit()) {
          <span class="text-xs text-ink-muted font-mono">{{ unit() }}</span>
        }
      </div>
      @if (sublabel()) {
        <span class="text-xs text-ink-dim">{{ sublabel() }}</span>
      }
    </div>
  `,
})
export class SecurityMetricCardComponent {
  label = input.required<string>();
  value = input.required<string | number>();
  unit = input<string>('');
  sublabel = input<string>('');
  trendLabel = input<string>('');
  tone = input<MetricTone>('live');

  toneClass(): string {
    return TONE_TEXT[this.tone()];
  }
}
