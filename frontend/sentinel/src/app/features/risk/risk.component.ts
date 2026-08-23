import { Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { RiskService } from '../../core/services/risk.service';
import { AIService } from '../../core/services/ai.service';
import { RiskGaugeComponent } from '../../shared/components/risk-gauge/risk-gauge.component';

@Component({
  selector: 'stn-risk',
  standalone: true,
  imports: [RiskGaugeComponent],
  template: `
    <div class="space-y-5">
      <div>
        <h1 class="text-lg font-semibold text-ink">User Risk & AI Behavioral Intelligence Center</h1>
        <p class="mono-label mt-1">Sentinel AI Engine — Anomaly Detection & Adaptive Risk Scoring</p>
      </div>

      <!-- AI Threat Intelligence Panel -->
      @if (aiIntel(); as ai) {
        <div class="bracket p-5 bg-void-800/40 border-signal-intel/40">
          <div class="flex items-center justify-between mb-4">
            <div class="flex items-center gap-2">
              <span class="inline-block w-2.5 h-2.5 rounded-full bg-cyan-400 animate-pulse"></span>
              <h2 class="text-sm font-semibold text-ink uppercase tracking-wider">AI Threat Intelligence Panel</h2>
            </div>
            <div class="flex items-center gap-3">
              <span class="mono-label text-[11px]">AI Confidence: <strong class="text-cyan-400 font-mono">{{ ai.aiConfidence }}%</strong></span>
              <span class="px-2 py-0.5 rounded text-xs font-mono font-bold"
                    [class]="ai.behaviorStatus === 'ABNORMAL' ? 'bg-red-950 text-red-400 border border-red-800' : 'bg-emerald-950 text-emerald-400 border border-emerald-800'">
                STATUS: {{ ai.behaviorStatus }}
              </span>
            </div>
          </div>

          <div class="p-3 bg-void-900/60 rounded border border-void-700 mb-4">
            <div class="text-xs text-ink-muted mono-label mb-1">Primary Behavioral Reason:</div>
            <div class="text-sm text-cyan-200 font-mono">{{ ai.reason }}</div>
          </div>

          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <div class="mono-label text-[11px] mb-2 text-ink-dim">Behavioral Risk Factors</div>
              <div class="space-y-2">
                @for (f of ai.factors; track f.name) {
                  <div class="flex items-center justify-between text-xs p-2 bg-void-900/40 rounded border border-void-700/50">
                    <span class="text-ink font-mono">{{ f.name }}</span>
                    <span class="font-mono text-cyan-400 font-semibold">{{ f.impact }} ({{ f.score }})</span>
                  </div>
                }
              </div>
            </div>

            <div>
              <div class="mono-label text-[11px] mb-2 text-ink-dim">Location Anomaly Timeline</div>
              <div class="space-y-2">
                @for (item of ai.timeline; track item.timestamp) {
                  <div class="flex items-center justify-between text-xs p-2 bg-void-900/40 rounded border border-void-700/50">
                    <div class="flex items-center gap-2">
                      <span class="font-mono text-ink-dim">{{ item.timestamp }}</span>
                      <span class="text-ink font-mono">{{ item.location }}</span>
                    </div>
                    <span class="font-mono text-[10px] px-1.5 py-0.5 rounded"
                          [class]="item.status === 'SAFE' ? 'text-emerald-400 bg-emerald-950/60' : 'text-red-400 bg-red-950/60'">
                      {{ item.status }}
                    </span>
                  </div>
                }
              </div>
            </div>
          </div>
        </div>
      }

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
  private aiService = inject(AIService);

  profile = toSignal(this.riskService.getUserRisk(), { initialValue: null });
  aiIntel = this.aiService.intelligence;
}
