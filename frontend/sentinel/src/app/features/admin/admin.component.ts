import { Component, signal } from '@angular/core';
import { Role } from '../../core/models/security.model';

interface ManagedUser {
  name: string;
  email: string;
  role: Role;
  status: 'ACTIVE' | 'SUSPENDED';
  riskScore: number;
}

const USERS: ManagedUser[] = [
  { name: 'Nanda Kishore', email: 'n.kishore@sentinel.io', role: 'SECURITY_ANALYST', status: 'ACTIVE', riskScore: 18 },
  { name: 'Valmiki Jaya Krishna', email: 'v.jayakrishna@sentinel.io', role: 'SECURITY_ANALYST', status: 'ACTIVE', riskScore: 12 },
  { name: 'Shaik Mohammed Bilal', email: 's.bilal@sentinel.io', role: 'USER', status: 'ACTIVE', riskScore: 34 },
  { name: 'System Admin', email: 'admin@sentinel.io', role: 'ADMIN', status: 'ACTIVE', riskScore: 5 },
  { name: 'svc-billing', email: 'svc-billing@sentinel.io', role: 'USER', status: 'SUSPENDED', riskScore: 91 },
];

const ROLE_STYLE: Record<Role, string> = {
  ADMIN: 'text-signal-intel',
  SECURITY_ANALYST: 'text-signal-live',
  USER: 'text-ink-muted',
};

@Component({
  selector: 'stn-admin',
  standalone: true,
  template: `
    <div class="space-y-5">
      <div class="flex items-center justify-between">
        <div>
          <h1 class="text-lg font-semibold text-ink">Admin Control Center</h1>
          <p class="mono-label mt-1">User, role, permission &amp; system configuration</p>
        </div>
        <span class="font-mono text-[10px] px-2 py-1 rounded-sm border border-signal-intel/40 text-signal-intel">ADMIN ACCESS</span>
      </div>

      <div class="grid grid-cols-3 gap-4">
        @for (r of roleCounts(); track r.role) {
          <div class="bracket p-4">
            <div class="mono-label">{{ r.role }}</div>
            <div class="font-mono text-2xl font-semibold text-ink mt-2">{{ r.count }}</div>
          </div>
        }
      </div>

      <div class="bracket overflow-hidden">
        <div class="mono-label px-4 py-3 border-b border-line">User Management</div>
        <table class="w-full text-xs">
          <thead>
            <tr class="text-ink-dim border-b border-line-soft">
              <th class="text-left font-mono font-normal px-4 py-2">Name</th>
              <th class="text-left font-mono font-normal px-4 py-2">Email</th>
              <th class="text-left font-mono font-normal px-4 py-2">Role</th>
              <th class="text-left font-mono font-normal px-4 py-2">Status</th>
              <th class="text-left font-mono font-normal px-4 py-2">Risk</th>
              <th class="text-left font-mono font-normal px-4 py-2"></th>
            </tr>
          </thead>
          <tbody>
            @for (u of users; track u.email) {
              <tr class="border-b border-line-soft hover:bg-void-700/30">
                <td class="px-4 py-2.5 text-ink">{{ u.name }}</td>
                <td class="px-4 py-2.5 font-mono text-ink-dim">{{ u.email }}</td>
                <td class="px-4 py-2.5 font-mono" [class]="roleStyle(u.role)">{{ u.role }}</td>
                <td class="px-4 py-2.5">
                  <span
                    class="font-mono text-[10px]"
                    [class]="u.status === 'ACTIVE' ? 'text-signal-safe' : 'text-signal-critical'"
                  >{{ u.status }}</span>
                </td>
                <td class="px-4 py-2.5 font-mono text-ink-muted">{{ u.riskScore }}</td>
                <td class="px-4 py-2.5 text-right">
                  <button class="font-mono text-[10px] text-ink-muted hover:text-signal-live">Manage</button>
                </td>
              </tr>
            }
          </tbody>
        </table>
      </div>

      <div class="bracket p-5">
        <div class="mono-label mb-4">System Configuration</div>
        <div class="grid grid-cols-2 md:grid-cols-4 gap-4 text-xs">
          <div>
            <div class="text-ink-muted">MFA Enforcement</div>
            <div class="font-mono text-signal-safe mt-1">ENABLED</div>
          </div>
          <div>
            <div class="text-ink-muted">Session Timeout</div>
            <div class="font-mono text-ink mt-1">30 min</div>
          </div>
          <div>
            <div class="text-ink-muted">Kafka Retention</div>
            <div class="font-mono text-ink mt-1">14 days</div>
          </div>
          <div>
            <div class="text-ink-muted">Alert Auto-Escalation</div>
            <div class="font-mono text-signal-safe mt-1">ENABLED</div>
          </div>
        </div>
      </div>
    </div>
  `,
})
export class AdminComponent {
  users = USERS;
  roleStyle = (r: Role) => ROLE_STYLE[r];

  roleCounts = signal(
    (['ADMIN', 'SECURITY_ANALYST', 'USER'] as Role[]).map((role) => ({
      role,
      count: USERS.filter((u) => u.role === role).length,
    })),
  );
}
