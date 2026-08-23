import { CommonModule } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { IncidentStatus } from '../../core/models/incident.model';
import { IncidentService } from '../../core/services/incident.service';

const FILTERS: (IncidentStatus | 'ALL')[] = [
  'ALL',
  'OPEN',
  'ACKNOWLEDGED',
  'INVESTIGATING',
  'MITIGATED',
  'RESOLVED',
  'CLOSED'
];

@Component({
  selector: 'stn-incidents',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="space-y-6">
      <!-- Header Banner -->
      <div class="flex flex-col md:flex-row md:items-center justify-between gap-4 border-b border-line pb-4">
        <div>
          <div class="flex items-center gap-2">
            <h1 class="text-xl font-bold text-ink tracking-tight">SOC Incident Response Workspace</h1>
            <span class="px-2 py-0.5 rounded text-[10px] font-mono font-semibold bg-signal-live/15 text-signal-live border border-signal-live/30 uppercase">
              Phase 5 Live
            </span>
          </div>
          <p class="mono-label mt-1 text-ink-muted">Real-time attack correlation, evidence investigation timeline, and analyst response control</p>
        </div>

        <div class="flex items-center gap-3">
          <button
            (click)="triggerScenario()"
            [disabled]="triggering()"
            class="px-3.5 py-2 rounded-sm font-mono text-xs font-semibold bg-signal-critical/15 text-signal-critical border border-signal-critical/40 hover:bg-signal-critical/25 transition-all flex items-center gap-2 disabled:opacity-50"
          >
            @if (triggering()) {
              <span class="animate-spin text-sm">↻</span>
              <span>Simulating Attack...</span>
            } @else {
              <span>⚡ Trigger Impossible Travel Attack</span>
            }
          </button>

          <button
            (click)="incidentService.fetchIncidents()"
            class="px-3 py-2 rounded-sm font-mono text-xs font-semibold bg-void-700 text-ink-muted border border-line hover:text-ink hover:border-ink-dim transition-colors"
          >
            Refresh
          </button>
        </div>
      </div>

      <!-- SOC Metrics Grid -->
      <div class="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-6 gap-3">
        <div class="bracket p-4 bg-void-800/80">
          <div class="mono-label text-[10px]">TOTAL INCIDENTS</div>
          <div class="text-2xl font-mono font-bold text-ink mt-1">{{ totalCount() }}</div>
        </div>
        <div class="bracket p-4 bg-void-800/80 border-l-2 border-l-signal-critical">
          <div class="mono-label text-[10px] text-signal-critical">CRITICAL</div>
          <div class="text-2xl font-mono font-bold text-signal-critical mt-1">{{ criticalCount() }}</div>
        </div>
        <div class="bracket p-4 bg-void-800/80 border-l-2 border-l-signal-warning">
          <div class="mono-label text-[10px] text-signal-warning">HIGH</div>
          <div class="text-2xl font-mono font-bold text-signal-warning mt-1">{{ highCount() }}</div>
        </div>
        <div class="bracket p-4 bg-void-800/80 border-l-2 border-l-signal-live">
          <div class="mono-label text-[10px] text-signal-live">INVESTIGATING</div>
          <div class="text-2xl font-mono font-bold text-signal-live mt-1">{{ investigatingCount() }}</div>
        </div>
        <div class="bracket p-4 bg-void-800/80 border-l-2 border-l-signal-intel">
          <div class="mono-label text-[10px] text-signal-intel">OPEN</div>
          <div class="text-2xl font-mono font-bold text-signal-intel mt-1">{{ openCount() }}</div>
        </div>
        <div class="bracket p-4 bg-void-800/80 border-l-2 border-l-signal-safe">
          <div class="mono-label text-[10px] text-signal-safe">RESOLVED</div>
          <div class="text-2xl font-mono font-bold text-signal-safe mt-1">{{ resolvedCount() }}</div>
        </div>
      </div>

      <!-- Filter Controls -->
      <div class="flex flex-wrap items-center gap-2 border-b border-line pb-3">
        <span class="mono-label text-xs mr-2">Filter Status:</span>
        @for (f of filters; track f) {
          <button
            (click)="filter.set(f)"
            class="font-mono text-[10px] uppercase tracking-wider px-3 py-1.5 rounded-sm border transition-all"
            [class]="filter() === f
              ? 'border-signal-live bg-signal-live/15 text-signal-live font-semibold shadow-sm'
              : 'border-line text-ink-muted hover:text-ink hover:border-ink-dim bg-void-800/40'"
          >
            {{ f }}
          </button>
        }
      </div>

      <!-- Incident Cards List -->
      <div class="space-y-3">
        @for (inc of filteredIncidents(); track inc.id) {
          <div class="bracket p-5 bg-void-800/60 hover:bg-void-800 border-line hover:border-ink-dim/40 transition-all flex flex-col md:flex-row md:items-center justify-between gap-4">
            <div class="space-y-2 flex-1 min-w-0">
              <div class="flex flex-wrap items-center gap-2">
                <span class="font-mono text-xs font-bold text-signal-intel bg-signal-intel/15 px-2 py-0.5 rounded border border-signal-intel/30">
                  {{ inc.incidentNumber }}
                </span>
                <span
                  class="font-mono text-[10px] font-bold px-2 py-0.5 rounded uppercase"
                  [ngClass]="{
                    'bg-signal-critical/20 text-signal-critical border border-signal-critical/40': inc.severity === 'CRITICAL',
                    'bg-signal-warning/20 text-signal-warning border border-signal-warning/40': inc.severity === 'HIGH',
                    'bg-signal-intel/20 text-signal-intel border border-signal-intel/40': inc.severity === 'MEDIUM',
                    'bg-signal-safe/20 text-signal-safe border border-signal-safe/40': inc.severity === 'LOW'
                  }"
                >
                  {{ inc.severity }}
                </span>
                <span
                  class="font-mono text-[10px] font-semibold px-2 py-0.5 rounded uppercase border"
                  [ngClass]="{
                    'border-signal-critical/40 text-signal-critical bg-signal-critical/10': inc.status === 'OPEN',
                    'border-signal-warning/40 text-signal-warning bg-signal-warning/10': inc.status === 'ACKNOWLEDGED',
                    'border-signal-live/40 text-signal-live bg-signal-live/10': inc.status === 'INVESTIGATING',
                    'border-signal-intel/40 text-signal-intel bg-signal-intel/10': inc.status === 'MITIGATED',
                    'border-signal-safe/40 text-signal-safe bg-signal-safe/10': inc.status === 'RESOLVED' || inc.status === 'CLOSED'
                  }"
                >
                  {{ inc.status }}
                </span>
              </div>

              <h2 class="text-base font-semibold text-ink hover:text-signal-live transition-colors">
                <a [routerLink]="['/incidents', inc.id]">{{ inc.title }}</a>
              </h2>

              <p class="text-xs text-ink-muted line-clamp-2">{{ inc.description }}</p>

              <div class="flex flex-wrap items-center gap-4 text-xs font-mono text-ink-dim pt-1">
                <span>User: <strong class="text-ink">{{ inc.affectedUser || 'N/A' }}</strong></span>
                <span>IP: <strong class="text-ink">{{ inc.affectedIp || 'N/A' }}</strong></span>
                <span>Analyst: <strong class="text-ink">{{ inc.assignedAnalyst || 'UNASSIGNED' }}</strong></span>
                <span>Created: {{ inc.createdAt | date:'shortTime' }}</span>
              </div>
            </div>

            <div class="flex md:flex-col items-end justify-between md:justify-center gap-3 shrink-0 border-t md:border-t-0 md:border-l border-line pt-3 md:pt-0 md:pl-5">
              <div class="flex items-center gap-3">
                <div class="text-right">
                  <div class="mono-label text-[9px]">RISK SCORE</div>
                  <div class="font-mono text-lg font-bold text-signal-critical">{{ inc.riskScore }}/100</div>
                </div>
                <div class="text-right border-l border-line pl-3">
                  <div class="mono-label text-[9px]">AI CONFIDENCE</div>
                  <div class="font-mono text-lg font-bold text-signal-live">{{ inc.aiConfidence }}%</div>
                </div>
              </div>

              <a
                [routerLink]="['/incidents', inc.id]"
                class="px-4 py-2 rounded-sm font-mono text-xs font-bold bg-signal-intel/20 text-signal-intel border border-signal-intel/40 hover:bg-signal-intel/30 transition-all flex items-center gap-1.5"
              >
                <span>Investigate Workspace</span>
                <span class="text-sm">→</span>
              </a>
            </div>
          </div>
        }

        @if (filteredIncidents().length === 0) {
          <div class="bracket p-12 text-center text-ink-muted font-mono text-xs space-y-2">
            <div>No incidents found matching status filter "{{ filter() }}".</div>
            <div class="text-ink-dim">Click "Trigger Impossible Travel Attack" to simulate an incident.</div>
          </div>
        }
      </div>
    </div>
  `,
})
export class IncidentDashboardComponent {
  incidentService = inject(IncidentService);
  filters = FILTERS;
  filter = signal<IncidentStatus | 'ALL'>('ALL');
  triggering = signal<boolean>(false);

  filteredIncidents = computed(() => {
    const f = this.filter();
    const all = this.incidentService.incidents();
    if (f === 'ALL') return all;
    return all.filter((i) => i.status === f);
  });

  totalCount = computed(() => this.incidentService.incidents().length);
  criticalCount = computed(() => this.incidentService.incidents().filter((i) => i.severity === 'CRITICAL').length);
  highCount = computed(() => this.incidentService.incidents().filter((i) => i.severity === 'HIGH').length);
  investigatingCount = computed(() => this.incidentService.incidents().filter((i) => i.status === 'INVESTIGATING').length);
  openCount = computed(() => this.incidentService.incidents().filter((i) => i.status === 'OPEN').length);
  resolvedCount = computed(() => this.incidentService.incidents().filter((i) => i.status === 'RESOLVED' || i.status === 'CLOSED').length);

  triggerScenario(): void {
    this.triggering.set(true);
    this.incidentService.triggerTestScenario().subscribe({
      next: () => this.triggering.set(false),
      error: () => this.triggering.set(false)
    });
  }
}
