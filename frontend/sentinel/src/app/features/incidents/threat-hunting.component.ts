import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ThreatHuntingFilter, ThreatHuntingResult } from '../../core/models/incident.model';
import { IncidentService } from '../../core/services/incident.service';

@Component({
  selector: 'stn-threat-hunting',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  template: `
    <div class="space-y-6">
      <!-- Header Banner -->
      <div class="border-b border-line pb-4 flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <div class="flex items-center gap-2">
            <h1 class="text-xl font-bold text-ink tracking-tight">Threat Hunting Workbench</h1>
            <span class="px-2 py-0.5 rounded text-[10px] font-mono font-semibold bg-signal-warning/15 text-signal-warning border border-signal-warning/30 uppercase">
              Multi-Vector Analytics
            </span>
          </div>
          <p class="mono-label mt-1 text-ink-muted">Query security events across user accounts, IP indicators, device fingerprints, and threat risk scores</p>
        </div>

        <button
          (click)="runSearch()"
          [disabled]="searching()"
          class="px-4 py-2 rounded-sm font-mono text-xs font-bold bg-signal-live/20 text-signal-live border border-signal-live/40 hover:bg-signal-live/30 transition-all flex items-center gap-2 disabled:opacity-50"
        >
          @if (searching()) {
            <span class="animate-spin">↻</span>
            <span>Hunting...</span>
          } @else {
            <span>🔍 Execute Threat Search</span>
          }
        </button>
      </div>

      <!-- Multi-Criteria Query Filters Panel -->
      <div class="bracket p-5 bg-void-800/80 border-t-2 border-t-signal-intel space-y-4">
        <h2 class="font-mono text-xs font-bold text-ink uppercase tracking-wider border-b border-line pb-2">
          Search Filters & Parameters
        </h2>

        <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 font-mono text-xs">
          <div class="space-y-1">
            <label class="mono-label text-[10px]">USERNAME</label>
            <input
              type="text"
              [(ngModel)]="filter.username"
              placeholder="e.g. john_doe"
              class="w-full p-2.5 rounded bg-void-900 border border-line text-ink placeholder:text-ink-dim focus:outline-none focus:border-signal-live"
            />
          </div>

          <div class="space-y-1">
            <label class="mono-label text-[10px]">EMAIL ADDRESS</label>
            <input
              type="text"
              [(ngModel)]="filter.email"
              placeholder="e.g. user@sentinel.sec"
              class="w-full p-2.5 rounded bg-void-900 border border-line text-ink placeholder:text-ink-dim focus:outline-none focus:border-signal-live"
            />
          </div>

          <div class="space-y-1">
            <label class="mono-label text-[10px]">IP ADDRESS</label>
            <input
              type="text"
              [(ngModel)]="filter.ipAddress"
              placeholder="e.g. 203.0.113.195"
              class="w-full p-2.5 rounded bg-void-900 border border-line text-ink placeholder:text-ink-dim focus:outline-none focus:border-signal-live"
            />
          </div>

          <div class="space-y-1">
            <label class="mono-label text-[10px]">DEVICE / USER-AGENT</label>
            <input
              type="text"
              [(ngModel)]="filter.device"
              placeholder="e.g. MacBookPro / Linux"
              class="w-full p-2.5 rounded bg-void-900 border border-line text-ink placeholder:text-ink-dim focus:outline-none focus:border-signal-live"
            />
          </div>

          <div class="space-y-1">
            <label class="mono-label text-[10px]">EVENT TYPE / THREAT</label>
            <input
              type="text"
              [(ngModel)]="filter.eventType"
              placeholder="e.g. SQL_INJECTION / IMPOSSIBLE_TRAVEL"
              class="w-full p-2.5 rounded bg-void-900 border border-line text-ink placeholder:text-ink-dim focus:outline-none focus:border-signal-live"
            />
          </div>

          <div class="space-y-1">
            <label class="mono-label text-[10px]">MIN RISK SCORE: <strong class="text-signal-critical">{{ filter.minRiskScore || 0 }}</strong></label>
            <input
              type="range"
              min="0"
              max="100"
              [(ngModel)]="filter.minRiskScore"
              class="w-full h-2 bg-void-900 rounded accent-signal-critical"
            />
          </div>

          <div class="space-y-1">
            <label class="mono-label text-[10px]">START DATE</label>
            <input
              type="date"
              [(ngModel)]="filter.startDate"
              class="w-full p-2 rounded bg-void-900 border border-line text-ink focus:outline-none focus:border-signal-live"
            />
          </div>

          <div class="space-y-1">
            <label class="mono-label text-[10px]">END DATE</label>
            <input
              type="date"
              [(ngModel)]="filter.endDate"
              class="w-full p-2 rounded bg-void-900 border border-line text-ink focus:outline-none focus:border-signal-live"
            />
          </div>
        </div>

        <div class="flex items-center justify-end gap-2 pt-2 border-t border-line">
          <button
            (click)="resetFilters()"
            class="px-3 py-1.5 rounded font-mono text-xs text-ink-muted border border-line hover:text-ink"
          >
            Clear Filters
          </button>
          <button
            (click)="runSearch()"
            class="px-4 py-1.5 rounded font-mono text-xs font-bold bg-signal-intel/20 text-signal-intel border border-signal-intel/40 hover:bg-signal-intel/30"
          >
            Apply Query
          </button>
        </div>
      </div>

      <!-- Search Results Table -->
      <div class="bracket p-5 bg-void-800/60 space-y-4">
        <div class="flex items-center justify-between border-b border-line pb-2">
          <h2 class="font-mono text-xs font-bold text-ink uppercase tracking-wider">
            Threat Search Results ({{ results().length }})
          </h2>
          <span class="mono-label text-[10px]">CORRELATED EVENTS & INCIDENTS</span>
        </div>

        <div class="overflow-x-auto">
          <table class="w-full text-left font-mono text-xs">
            <thead>
              <tr class="border-b border-line text-ink-muted text-[10px] uppercase">
                <th class="py-2.5 px-3">Timestamp</th>
                <th class="py-2.5 px-3">User</th>
                <th class="py-2.5 px-3">IP Address</th>
                <th class="py-2.5 px-3">Event / Threat</th>
                <th class="py-2.5 px-3">Source</th>
                <th class="py-2.5 px-3 text-right">Risk Score</th>
                <th class="py-2.5 px-3 text-center">Severity</th>
                <th class="py-2.5 px-3 text-right">Action</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-line/40">
              @for (row of results(); track row.id) {
                <tr class="hover:bg-void-700/50 transition-colors">
                  <td class="py-3 px-3 text-ink-dim text-[11px] whitespace-nowrap">{{ row.timestamp | date:'short' }}</td>
                  <td class="py-3 px-3 font-semibold text-ink">{{ row.user || 'N/A' }}</td>
                  <td class="py-3 px-3 text-signal-warning">{{ row.ipAddress || 'N/A' }}</td>
                  <td class="py-3 px-3 font-bold text-signal-live">{{ row.eventType || row.title }}</td>
                  <td class="py-3 px-3 text-ink-muted">{{ row.source }}</td>
                  <td class="py-3 px-3 text-right font-bold text-signal-critical">{{ row.riskScore }}/100</td>
                  <td class="py-3 px-3 text-center">
                    <span
                      class="px-2 py-0.5 rounded text-[10px] font-bold uppercase"
                      [ngClass]="{
                        'bg-signal-critical/20 text-signal-critical border border-signal-critical/40': row.severity === 'CRITICAL',
                        'bg-signal-warning/20 text-signal-warning border border-signal-warning/40': row.severity === 'HIGH',
                        'bg-signal-intel/20 text-signal-intel border border-signal-intel/40': row.severity === 'MEDIUM'
                      }"
                    >
                      {{ row.severity }}
                    </span>
                  </td>
                  <td class="py-3 px-3 text-right">
                    @if (row.incidentId) {
                      <a
                        [routerLink]="['/incidents', row.incidentId]"
                        class="px-2.5 py-1 rounded text-[11px] font-bold bg-signal-intel/20 text-signal-intel border border-signal-intel/40 hover:bg-signal-intel/30"
                      >
                        View Incident →
                      </a>
                    } @else {
                      <span class="text-[10px] text-ink-dim">Event Only</span>
                    }
                  </td>
                </tr>
              }

              @if (results().length === 0) {
                <tr>
                  <td colspan="8" class="text-center py-8 text-ink-muted italic">
                    No threat events found matching the specified search criteria.
                  </td>
                </tr>
              }
            </tbody>
          </table>
        </div>
      </div>
    </div>
  `,
})
export class ThreatHuntingComponent implements OnInit {
  private incidentService = inject(IncidentService);

  filter: ThreatHuntingFilter = {
    username: '',
    email: '',
    ipAddress: '',
    device: '',
    eventType: '',
    minRiskScore: 0
  };

  results = signal<ThreatHuntingResult[]>([]);
  searching = signal<boolean>(false);

  ngOnInit(): void {
    this.runSearch();
  }

  runSearch(): void {
    this.searching.set(true);
    this.incidentService.searchThreats(this.filter).subscribe({
      next: (res) => {
        this.results.set(res);
        this.searching.set(false);
      },
      error: () => this.searching.set(false)
    });
  }

  resetFilters(): void {
    this.filter = {
      username: '',
      email: '',
      ipAddress: '',
      device: '',
      eventType: '',
      minRiskScore: 0
    };
    this.runSearch();
  }
}
