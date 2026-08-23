import { Component, computed, inject } from '@angular/core';
import { SecurityEventService } from '../../core/services/security-event.service';
import { ThreatService } from '../../core/services/threat.service';
import { SecurityMetricCardComponent } from '../../shared/components/security-metric-card/security-metric-card.component';
import { EventStreamComponent } from '../../shared/components/event-stream/event-stream.component';
import { ThreatTimelineComponent } from '../../shared/components/threat-timeline/threat-timeline.component';
import { RiskChartComponent, RiskChartPoint } from '../../shared/components/risk-chart/risk-chart.component';
import { SecurityMapComponent, RiskNode } from '../../shared/components/security-map/security-map.component';
import { RiskGaugeComponent } from '../../shared/components/risk-gauge/risk-gauge.component';
import { toSignal } from '@angular/core/rxjs-interop';
import { generateHealth } from '../../core/services/mock-data';

@Component({
  selector: 'stn-dashboard',
  standalone: true,
  imports: [
    SecurityMetricCardComponent,
    EventStreamComponent,
    ThreatTimelineComponent,
    RiskChartComponent,
    SecurityMapComponent,
    RiskGaugeComponent,
  ],
  template: `
    <div class="space-y-5">
      <div class="flex items-center justify-between">
        <div>
          <h1 class="text-lg font-semibold text-ink">Global Threat Overview</h1>
          <p class="mono-label mt-1">Live posture across Authentication, Monitoring &amp; Threat Detection</p>
        </div>
        <div class="flex items-center gap-2">
          <span class="status-dot bg-signal-live animate-heartbeat"></span>
          <span class="font-mono text-[10px] text-signal-live">PIPELINE NOMINAL</span>
        </div>
      </div>

      <!-- A. Global metrics -->
      <div class="grid grid-cols-2 lg:grid-cols-5 gap-4">
        <stn-security-metric-card label="Current Risk Score" [value]="riskScore" unit="/ 100" tone="warn" trendLabel="▲ 6" />
        <stn-security-metric-card label="Active Threats" [value]="threatCount()" tone="critical" trendLabel="▲ 2" />
        <stn-security-metric-card label="Events Today" [value]="eventsToday" tone="live" sublabel="via Kafka stream" />
        <stn-security-metric-card label="Critical Alerts" [value]="criticalCount()" tone="critical" trendLabel="▼ 1" />
        <stn-security-metric-card label="System Health" value="99.7" unit="%" tone="safe" sublabel="all services nominal" />
      </div>

      <div class="grid grid-cols-1 xl:grid-cols-3 gap-5">
        <!-- B. Live event stream -->
        <div class="xl:col-span-1 h-[560px]">
          <stn-event-stream [events]="events()" />
        </div>

        <!-- C. Threat intelligence -->
        <div class="xl:col-span-2 grid grid-cols-1 md:grid-cols-2 gap-5">
          <div class="md:col-span-2">
            <stn-threat-timeline [threats]="threats()" />
          </div>

          <stn-risk-chart title="Threat Severity Distribution" [points]="severityPoints()" />

          <div class="bracket p-4 flex flex-col items-center justify-center gap-3">
            <span class="mono-label self-start">Aggregate Risk Constellation</span>
            <stn-risk-gauge [score]="riskScore" [size]="140" />
          </div>

          <div class="md:col-span-2">
            <stn-security-map [nodes]="riskNodes" />
          </div>
        </div>
      </div>

      <!-- System health strip -->
      <div class="bracket p-4">
        <div class="mono-label mb-3">Detection Statistics — Pipeline Health</div>
        <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
          @for (h of health; track h.label) {
            <div>
              <div class="flex justify-between text-[11px] mb-1">
                <span class="text-ink-muted">{{ h.label }}</span>
                <span class="font-mono text-ink">{{ h.value }}%</span>
              </div>
              <div class="h-1 bg-void-700 rounded-sm overflow-hidden">
                <div class="h-full bg-signal-safe" [style.width.%]="h.value"></div>
              </div>
            </div>
          }
        </div>
      </div>
    </div>
  `,
})
export class DashboardComponent {
  private eventService = inject(SecurityEventService);
  private threatService = inject(ThreatService);

  events = this.eventService.events;
  threats = toSignal(this.threatService.getThreats(), { initialValue: [] });

  riskScore = 68;
  eventsToday = 1284;
  criticalCount = computed(() => this.events().filter((e) => e.severity === 'CRITICAL').length);
  threatCount = computed(() => this.threats().length);
  health = generateHealth();

  severityPoints = computed<RiskChartPoint[]>(() => {
    const counts: Record<string, number> = { SAFE: 0, LOW: 0, MEDIUM: 0, HIGH: 0, CRITICAL: 0 };
    for (const e of this.events()) counts[e.severity]++;
    return [
      { label: 'Critical', value: counts['CRITICAL'], colorVar: '#EF4444' },
      { label: 'High', value: counts['HIGH'], colorVar: '#F5A623' },
      { label: 'Medium', value: counts['MEDIUM'], colorVar: '#22D3EE' },
      { label: 'Low / Safe', value: counts['LOW'] + counts['SAFE'], colorVar: '#22C55E' },
    ];
  });

  riskNodes: RiskNode[] = Array.from({ length: 24 }, (_, i) => ({
    label: `usr-${1000 + i}`,
    score: Math.floor(Math.random() * 100),
  }));
}
