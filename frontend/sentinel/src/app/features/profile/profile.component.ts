import { Component, inject } from '@angular/core';
import { AuthService } from '../../core/services/auth.service';
import { RiskGaugeComponent } from '../../shared/components/risk-gauge/risk-gauge.component';
import { ActivityTimelineComponent, ActivityItem } from '../../shared/components/activity-timeline/activity-timeline.component';

const SESSIONS = [
  { device: 'MacBook Pro — Chrome 128', location: 'Bengaluru, IN', current: true },
  { device: 'iPhone 15 — Sentinel App', location: 'Bengaluru, IN', current: false },
];

const LOGIN_HISTORY: ActivityItem[] = [
  { timestamp: new Date(Date.now() - 3600_000).toISOString(), label: 'Login success', detail: 'Bengaluru, IN · MFA verified' },
  { timestamp: new Date(Date.now() - 26 * 3600_000).toISOString(), label: 'Login success', detail: 'Bengaluru, IN · MFA verified' },
  { timestamp: new Date(Date.now() - 50 * 3600_000).toISOString(), label: 'Password changed', detail: 'Initiated from settings' },
  { timestamp: new Date(Date.now() - 96 * 3600_000).toISOString(), label: 'Login failed', detail: 'Unknown location · blocked' },
];

@Component({
  selector: 'stn-profile',
  standalone: true,
  imports: [RiskGaugeComponent, ActivityTimelineComponent],
  template: `
    <div class="space-y-5">
      <div>
        <h1 class="text-lg font-semibold text-ink">Security Profile</h1>
        <p class="mono-label mt-1">Identity, sessions, and behavioral risk for your account</p>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-3 gap-5">
        <div class="bracket p-6 flex flex-col items-center gap-4">
          <div
            class="w-16 h-16 rounded-sm bg-signal-intel/10 border border-signal-intel/40 flex items-center justify-center font-mono text-lg text-signal-intel font-bold"
          >
            {{ initials() }}
          </div>
          <div class="text-center">
            <div class="text-sm font-semibold text-ink">{{ auth.user()?.name }}</div>
            <div class="text-xs text-ink-muted mt-0.5">{{ auth.user()?.email }}</div>
            <div class="mono-label mt-1">{{ auth.user()?.role }}</div>
          </div>
          <stn-risk-gauge [score]="auth.user()?.riskScore || 0" [size]="120" />
        </div>

        <div class="lg:col-span-2 bracket p-5">
          <div class="mono-label mb-4">Active Sessions</div>
          <div class="space-y-3">
            @for (s of sessions; track s.device) {
              <div class="flex items-center justify-between divider-hairline pb-3 border-b last:border-0 last:pb-0">
                <div>
                  <div class="text-sm text-ink">{{ s.device }}</div>
                  <div class="text-[11px] text-ink-muted">{{ s.location }}</div>
                </div>
                @if (s.current) {
                  <span class="font-mono text-[10px] text-signal-safe">THIS DEVICE</span>
                } @else {
                  <button class="font-mono text-[10px] text-signal-critical hover:underline">Revoke</button>
                }
              </div>
            }
          </div>
        </div>

        <div class="lg:col-span-3">
          <stn-activity-timeline title="Login History &amp; Security Events" [items]="history" />
        </div>
      </div>
    </div>
  `,
})
export class ProfileComponent {
  auth = inject(AuthService);
  sessions = SESSIONS;
  history = LOGIN_HISTORY;

  initials(): string {
    const name = this.auth.user()?.name ?? '';
    return name
      .split(' ')
      .map((p) => p[0])
      .slice(0, 2)
      .join('')
      .toUpperCase();
  }
}
