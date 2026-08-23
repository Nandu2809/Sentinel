import { Component, computed, input } from '@angular/core';

@Component({
  selector: 'stn-risk-gauge',
  standalone: true,
  template: `
    <div class="relative flex items-center justify-center" [style.width.px]="size()" [style.height.px]="size()">
      <svg [attr.viewBox]="'0 0 ' + size() + ' ' + size()" class="-rotate-90">
        <circle
          [attr.cx]="size() / 2"
          [attr.cy]="size() / 2"
          [attr.r]="radius()"
          fill="none"
          stroke="#1E293B"
          [attr.stroke-width]="stroke()"
        />
        @for (tick of ticks(); track tick) {
          <line
            [attr.x1]="size() / 2 + (radius() - stroke() / 2 - 2) * cos(tick)"
            [attr.y1]="size() / 2 + (radius() - stroke() / 2 - 2) * sin(tick)"
            [attr.x2]="size() / 2 + (radius() + stroke() / 2 + 2) * cos(tick)"
            [attr.y2]="size() / 2 + (radius() + stroke() / 2 + 2) * sin(tick)"
            stroke="#0A0D12"
            stroke-width="2"
          />
        }
        <circle
          [attr.cx]="size() / 2"
          [attr.cy]="size() / 2"
          [attr.r]="radius()"
          fill="none"
          [attr.stroke]="color()"
          [attr.stroke-width]="stroke()"
          stroke-linecap="butt"
          [attr.stroke-dasharray]="circumference()"
          [attr.stroke-dashoffset]="dashOffset()"
          class="transition-all duration-700 ease-out"
          [style.filter]="'drop-shadow(0 0 6px ' + color() + ')'"
        />
      </svg>
      <div class="absolute inset-0 flex flex-col items-center justify-center">
        <span class="font-mono text-2xl font-bold" [style.color]="color()">{{ score() }}</span>
        <span class="mono-label mt-0.5">RISK / 100</span>
      </div>
    </div>
  `,
})
export class RiskGaugeComponent {
  score = input.required<number>();
  size = input<number>(160);

  radius = computed(() => this.size() / 2 - 14);
  stroke = computed(() => 8);
  circumference = computed(() => 2 * Math.PI * this.radius());
  dashOffset = computed(() => this.circumference() * (1 - this.score() / 100));

  ticks = computed(() =>
    Array.from({ length: 20 }, (_, i) => (i / 20) * 2 * Math.PI),
  );

  cos(angle: number): number {
    return Math.cos(angle);
  }
  sin(angle: number): number {
    return Math.sin(angle);
  }

  color(): string {
    const s = this.score();
    if (s >= 80) return '#EF4444';
    if (s >= 55) return '#F5A623';
    if (s >= 25) return '#22D3EE';
    return '#22C55E';
  }
}
