import { Component, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../core/services/auth.service';

interface NavItem {
  path: string;
  label: string;
  glyph: string;
}

const NAV: NavItem[] = [
  { path: '/dashboard', label: 'Operations', glyph: 'OPS' },
  { path: '/incidents', label: 'Incidents', glyph: 'INC' },
  { path: '/threat-hunting', label: 'Hunting', glyph: 'HNT' },
  { path: '/alerts', label: 'Alerts', glyph: 'ALT' },
  { path: '/threats', label: 'Threats', glyph: 'THR' },
  { path: '/risk', label: 'Risk Intel', glyph: 'RSK' },
  { path: '/profile', label: 'Profile', glyph: 'USR' },
  { path: '/admin', label: 'Admin', glyph: 'ADM' },
];

@Component({
  selector: 'stn-shell',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  template: `
    <div class="min-h-screen flex bg-void-900">
      <!-- Icon rail nav -->
      <nav class="w-16 shrink-0 flex flex-col items-center py-4 gap-1 border-r border-line bg-void-800/60">
        <div class="w-8 h-8 rounded-sm bg-signal-intel/15 border border-signal-intel/40 flex items-center justify-center mb-6">
          <span class="font-mono text-[10px] font-bold text-signal-intel">S</span>
        </div>
        @for (item of nav; track item.path) {
          <a
            [routerLink]="item.path"
            routerLinkActive="bg-void-700 text-signal-live"
            class="w-11 h-11 rounded-sm flex flex-col items-center justify-center gap-0.5 text-ink-dim hover:text-ink hover:bg-void-700/60 transition-colors"
          >
            <span class="font-mono text-[9px] font-semibold tracking-wide">{{ item.glyph }}</span>
          </a>
        }
        <div class="mt-auto">
          <button
            (click)="logout()"
            class="w-11 h-11 rounded-sm flex items-center justify-center text-ink-dim hover:text-signal-critical hover:bg-signal-critical/10 transition-colors"
            title="Sign out"
          >
            <span class="font-mono text-[9px] font-semibold">OUT</span>
          </button>
        </div>
      </nav>

      <div class="flex-1 flex flex-col min-w-0">
        <!-- Top status bar -->
        <header class="h-12 shrink-0 flex items-center justify-between px-5 border-b border-line bg-void-800/60">
          <div class="flex items-center gap-3">
            <span class="font-mono text-xs font-semibold tracking-[0.2em] text-ink">SENTINEL</span>
            <span class="mono-label">Security Intelligence Platform</span>
          </div>
          <div class="flex items-center gap-5">
            <span class="flex items-center gap-1.5">
              <span class="status-dot bg-signal-safe animate-heartbeat"></span>
              <span class="font-mono text-[10px] text-signal-safe">ALL SYSTEMS NOMINAL</span>
            </span>
            <span class="font-mono text-[11px] text-ink-muted">{{ auth.user()?.name }}</span>
            <span class="font-mono text-[10px] text-ink-dim uppercase">{{ auth.role() }}</span>
          </div>
        </header>

        <main class="flex-1 overflow-y-auto p-5">
          <router-outlet />
        </main>
      </div>
    </div>
  `,
})
export class ShellComponent {
  nav = NAV;
  auth = inject(AuthService);
  private router = inject(Router);

  logout(): void {
    this.auth.logout();
    this.router.navigate(['/login']);
  }
}
