import { Component, computed, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ThreatService } from '../../core/services/threat.service';
import { ThreatTimelineComponent } from '../../shared/components/threat-timeline/threat-timeline.component';
import { RiskChartComponent, RiskChartPoint } from '../../shared/components/risk-chart/risk-chart.component';
import { SeverityBadgeComponent } from '../../shared/components/severity-badge/severity-badge.component';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'stn-threats',
  standalone: true,
  imports: [ThreatTimelineComponent, RiskChartComponent, SeverityBadgeComponent, DatePipe],
  template: `
    <div class="space-y-5">
      <div>
        <h1 class="text-lg font-semibold text-ink">Threat Analysis</h1>
        <p class="mono-label mt-1">Threat Detection Engine — categorized output &amp; confidence scoring</p>
      </div>

      <div class="grid grid-cols-1 xl:grid-cols-3 gap-5">
        <div class="xl:col-span-2">
          <stn-threat-timeline [threats]="threats()" />
        </div>
        <stn-risk-chart title="Detection Confidence by Category" [points]="confidencePoints()" />
      </div>

      <div class="bracket overflow-hidden">
        <div class="mono-label px-4 py-3 border-b border-line">Historical Analysis</div>
        <table class="w-full text-xs">
          <thead>
            <tr class="text-ink-dim border-b border-line-soft">
              <th class="text-left font-mono font-normal px-4 py-2">ID</th>
              <th class="text-left font-mono font-normal px-4 py-2">Category</th>
              <th class="text-left font-mono font-normal px-4 py-2">Severity</th>
              <th class="text-left font-mono font-normal px-4 py-2">Confidence</th>
              <th class="text-left font-mono font-normal px-4 py-2">First Seen</th>
              <th class="text-left font-mono font-normal px-4 py-2">Occurrences</th>
            </tr>
          </thead>
          <tbody>
            @for (t of threats(); track t.id) {
              <tr class="border-b border-line-soft hover:bg-void-700/30">
                <td class="px-4 py-2.5 font-mono text-ink-dim">{{ t.id }}</td>
                <td class="px-4 py-2.5 text-ink">{{ t.category }}</td>
                <td class="px-4 py-2.5"><stn-severity-badge [severity]="t.severity" /></td>
                <td class="px-4 py-2.5 font-mono text-ink-muted">{{ t.confidence }}%</td>
                <td class="px-4 py-2.5 font-mono text-ink-dim">{{ t.firstSeen | date: 'MMM d, HH:mm' }}</td>
                <td class="px-4 py-2.5 font-mono text-ink-muted">{{ t.occurrences }}</td>
              </tr>
            }
          </tbody>
        </table>
      </div>
    </div>
  `,
})
export class ThreatsComponent {
  private threatService = inject(ThreatService);
  threats = toSignal(this.threatService.getThreats(), { initialValue: [] });

  confidencePoints = computed<RiskChartPoint[]>(() =>
    this.threats().map((t) => ({
      label: t.category,
      value: t.confidence,
      colorVar: t.severity === 'CRITICAL' ? '#EF4444' : t.severity === 'HIGH' ? '#F5A623' : '#8B5CF6',
    })),
  );
}
