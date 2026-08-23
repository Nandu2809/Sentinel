import { Component, computed, input } from '@angular/core';

export interface RiskChartPoint {
  label: string;
  value: number;
  colorVar?: string;
}

@Component({
  selector: 'stn-risk-chart',
  standalone: true,
  template: `
    <div class="bracket p-4">
      <div class="mono-label mb-4">{{ title() }}</div>
      <div class="space-y-3">
        @for (p of points(); track p.label) {
          <div>
            <div class="flex justify-between text-[11px] mb-1">
              <span class="text-ink-muted font-mono">{{ p.label }}</span>
              <span class="text-ink font-mono">{{ p.value }}</span>
            </div>
            <div class="h-1.5 bg-void-700 rounded-sm overflow-hidden">
              <div
                class="h-full rounded-sm transition-all duration-700"
                [style.width.%]="pct(p.value)"
                [style.backgroundColor]="p.colorVar || '#22D3EE'"
              ></div>
            </div>
          </div>
        }
      </div>
    </div>
  `,
})
export class RiskChartComponent {
  title = input<string>('Threat Severity Distribution');
  points = input.required<RiskChartPoint[]>();

  max = computed(() => Math.max(...this.points().map((p) => p.value), 1));

  pct(value: number): number {
    return (value / this.max()) * 100;
  }
}
