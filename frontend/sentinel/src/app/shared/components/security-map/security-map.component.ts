import { Component, input } from '@angular/core';

export interface RiskNode {
  label: string;
  score: number;
}

@Component({
  selector: 'stn-security-map',
  standalone: true,
  template: `
    <div class="bracket p-4">
      <div class="mono-label mb-4">User Risk Map</div>
      <div class="grid grid-cols-6 gap-2">
        @for (n of nodes(); track n.label) {
          <div
            class="aspect-square rounded-sm flex items-center justify-center relative group cursor-default"
            [style.backgroundColor]="bg(n.score)"
            [title]="n.label + ' — ' + n.score"
          >
            <span class="font-mono text-[9px] text-void-900 font-bold opacity-80">{{ n.score }}</span>
          </div>
        }
      </div>
      <div class="flex items-center gap-4 mt-4 font-mono text-[10px] text-ink-dim">
        <span class="flex items-center gap-1"><span class="w-2 h-2 rounded-sm" style="background:#22C55E"></span>Low</span>
        <span class="flex items-center gap-1"><span class="w-2 h-2 rounded-sm" style="background:#F5A623"></span>Elevated</span>
        <span class="flex items-center gap-1"><span class="w-2 h-2 rounded-sm" style="background:#EF4444"></span>Critical</span>
      </div>
    </div>
  `,
})
export class SecurityMapComponent {
  nodes = input.required<RiskNode[]>();

  bg(score: number): string {
    if (score >= 80) return '#EF4444';
    if (score >= 50) return '#F5A623';
    return '#22C55E';
  }
}
