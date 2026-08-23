import { Component, computed, inject, signal } from '@angular/core';
import { AlertService } from '../../core/services/alert.service';
import { AlertPanelComponent } from '../../shared/components/alert-panel/alert-panel.component';
import { AlertStatus } from '../../core/models/security.model';

const FILTERS: (AlertStatus | 'ALL')[] = ['ALL', 'NEW', 'INVESTIGATING', 'ACKNOWLEDGED', 'RESOLVED'];

@Component({
  selector: 'stn-alerts',
  standalone: true,
  imports: [AlertPanelComponent],
  template: `
    <div class="space-y-5">
      <div>
        <h1 class="text-lg font-semibold text-ink">Alert Management</h1>
        <p class="mono-label mt-1">Every alert is a live incident — acknowledge, assign, and resolve</p>
      </div>

      <div class="flex gap-2">
        @for (f of filters; track f) {
          <button
            (click)="filter.set(f)"
            class="font-mono text-[10px] uppercase tracking-wider px-3 py-1.5 rounded-sm border transition-colors"
            [class]="filter() === f
              ? 'border-signal-live/50 bg-signal-live/10 text-signal-live'
              : 'border-line text-ink-muted hover:text-ink hover:border-ink-dim'"
          >
            {{ f }}
          </button>
        }
      </div>

      <div class="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-4">
        @for (alert of filtered(); track alert.id) {
          <stn-alert-panel
            [alert]="alert"
            (acknowledge)="alertService.acknowledge($event)"
            (investigate)="alertService.investigate($event)"
            (resolve)="alertService.resolve($event)"
          />
        }
      </div>

      @if (filtered().length === 0) {
        <div class="bracket p-8 text-center text-sm text-ink-muted">No incidents match this filter.</div>
      }
    </div>
  `,
})
export class AlertsComponent {
  alertService = inject(AlertService);
  filters = FILTERS;
  filter = signal<AlertStatus | 'ALL'>('ALL');

  filtered = computed(() => {
    const f = this.filter();
    const all = this.alertService.alerts();
    return f === 'ALL' ? all : all.filter((a) => a.status === f);
  });
}
