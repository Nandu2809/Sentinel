import { Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { RiskService } from '../../core/services/risk.service';
import { RiskGaugeComponent } from '../../shared/components/risk-gauge/risk-gauge.component';

@Component({
  selector: 'stn-risk',
  standalone: true,
  imports: [RiskGaugeComponent],
  template: `
    <div class="space-y-5">
      <div>
        <h1 class="text-lg font-semibold text-ink">User Risk Intelligence Center</h1>
        <p class="mono-label mt-1">Risk Intelligence Engine — composite behavioral scoring</p>
      </div>

      @if (profile(); as p) {
        <div class="grid grid-cols-1 lg:grid-cols-3 gap-5">
          <div class="bracket p-6 flex flex-col items-center justify-center gap-4">
            <div class="text-center">
              <div class="text-sm font-semibold text-ink">{{ p.name }}</div>
              <div class="mono-label mt-1">{{ p.userId }}</div>
            </div>
            <stn-risk-gauge [score]="p.score" [size]="180" />
          </div>

          <div class="lg:col-span-2 bracket p-5">
            <div class="mono-label mb-4">Contributing Factors</div>
            <div class="space-y-3">
              @for (f of p.factors; track f.label) {
                <div class="flex items-center justify-between">
                  <div class="flex items-center gap-2">
                    <span
                      class="font-mono text-xs"
                      [class]="f.direction === 'up' ? 'text-signal-critical' : 'text-signal-safe'"
                    >{{ f.direction === 'up' ? '+' : '−' }}</span>
                    <span class="text-sm text-ink">{{ f.label }}</span>
                  </div>
                  <div class="flex items-center gap-2 w-40">
                    <div class="flex-1 h-1.5 bg-void-700 rounded-sm overflow-hidden">
                      <div
                        class="h-full rounded-sm"
                        [class]="f.direction === 'up' ? 'bg-signal-critical' : 'bg-signal-safe'"
                        [style.width.%]="f.weight * 3"
                      ></div>
                    </div>
                    <span class="font-mono text-[11px] text-ink-muted w-8 text-right">{{ f.weight }}</span>
                  </div>
                </div>
              }
            </div>
          </div>

          <div class="lg:col-span-3 bracket p-5">
            <div class="mono-label mb-4">Score Trend — Last 8 Evaluations</div>
            <div class="flex items-end gap-2 h-32">
              @for (v of p.trend; track $index) {
                <div class="flex-1 flex flex-col items-center gap-2">
                  <div
                    class="w-full rounded-t-sm bg-signal-intel/60"
                    [style.height.%]="v"
                  ></div>
                  <span class="font-mono text-[9px] text-ink-dim">{{ v }}</span>
                </div>
              }
            </div>
          </div>
        </div>
      }
    </div>
  `,
})
export class RiskComponent {
  private riskService = inject(RiskService);
  profile = toSignal(this.riskService.getUserRisk(), { initialValue: null });
}
