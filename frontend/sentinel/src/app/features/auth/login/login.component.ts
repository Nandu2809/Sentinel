import { Component, inject, signal } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'stn-login',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  template: `
    <div class="min-h-screen flex bg-void-900">
      <!-- Network intelligence panel -->
      <div class="hidden lg:flex flex-1 relative overflow-hidden border-r border-line bg-void-950">
        <svg class="absolute inset-0 w-full h-full opacity-70" viewBox="0 0 800 800" preserveAspectRatio="xMidYMid slice">
          @for (link of links; track $index) {
            <line
              [attr.x1]="link.x1" [attr.y1]="link.y1" [attr.x2]="link.x2" [attr.y2]="link.y2"
              stroke="#1B2438" stroke-width="1"
            />
          }
          @for (node of nodes; track $index) {
            <circle [attr.cx]="node.x" [attr.cy]="node.y" [attr.r]="node.r" fill="#22D3EE" opacity="0.5">
              <animate attributeName="opacity" [attr.values]="'0.2;0.8;0.2'" [attr.dur]="node.dur + 's'" repeatCount="indefinite" />
            </circle>
          }
        </svg>
        <div class="absolute inset-0 bg-gradient-to-t from-void-950 via-transparent to-void-950/60"></div>

        <div class="relative z-10 flex flex-col justify-between p-12 w-full">
          <div class="flex items-center gap-2">
            <div class="w-9 h-9 rounded-sm bg-signal-intel/15 border border-signal-intel/40 flex items-center justify-center">
              <span class="font-mono text-xs font-bold text-signal-intel">S</span>
            </div>
            <span class="font-mono text-sm font-semibold tracking-[0.25em] text-ink">SENTINEL</span>
          </div>

          <div>
            <div class="flex items-center gap-2 mb-4">
              <span class="status-dot bg-signal-safe animate-heartbeat"></span>
              <span class="font-mono text-[11px] text-signal-safe tracking-wide">SYSTEM AVAILABILITY 99.98%</span>
            </div>
            <h1 class="text-2xl font-semibold text-ink max-w-md leading-snug">
              Connecting to Sentinel Security Intelligence Network
            </h1>
            <p class="mt-3 text-sm text-ink-muted max-w-sm">
              Continuous behavioral monitoring, AI threat detection, and risk intelligence for every identity on your network.
            </p>
          </div>
        </div>
      </div>

      <!-- Auth panel -->
      <div class="w-full lg:w-[440px] shrink-0 flex flex-col justify-center px-10 py-12">
        <div class="lg:hidden flex items-center gap-2 mb-10">
          <div class="w-8 h-8 rounded-sm bg-signal-intel/15 border border-signal-intel/40 flex items-center justify-center">
            <span class="font-mono text-xs font-bold text-signal-intel">S</span>
          </div>
          <span class="font-mono text-sm font-semibold tracking-[0.25em] text-ink">SENTINEL</span>
        </div>

        <div class="mono-label mb-2">Secure Access Gateway</div>
        <h2 class="text-xl font-semibold text-ink mb-1">Sign in to your console</h2>
        <p class="text-sm text-ink-muted mb-8">Authenticate to resume monitoring your organization's security posture.</p>

        <form [formGroup]="form" (ngSubmit)="submit()" class="space-y-4">
          <div>
            <label class="mono-label block mb-1.5">Email</label>
            <input
              type="email"
              formControlName="email"
              class="w-full bg-void-800 border border-line rounded-sm px-3 py-2.5 text-sm text-ink placeholder:text-ink-dim focus:outline-none focus:ring-1 focus:ring-signal-live focus:border-signal-live"
              placeholder="analyst@organization.com"
            />
          </div>
          <div>
            <label class="mono-label block mb-1.5">Password</label>
            <input
              type="password"
              formControlName="password"
              class="w-full bg-void-800 border border-line rounded-sm px-3 py-2.5 text-sm text-ink placeholder:text-ink-dim focus:outline-none focus:ring-1 focus:ring-signal-live focus:border-signal-live"
              placeholder="••••••••••••"
            />
          </div>

          @if (error()) {
            <div class="text-xs text-signal-critical font-mono">{{ error() }}</div>
          }

          <button
            type="submit"
            [disabled]="form.invalid || loading()"
            class="w-full bg-signal-live/10 hover:bg-signal-live/20 disabled:opacity-40 border border-signal-live/40 text-signal-live font-mono text-xs uppercase tracking-wider py-3 rounded-sm transition-colors"
          >
            {{ loading() ? 'Authenticating…' : 'Authenticate' }}
          </button>
        </form>

        <div class="mt-6 flex items-center justify-between text-xs text-ink-muted">
          <span>New to Sentinel?</span>
          <a routerLink="/register" class="text-signal-live hover:underline">Request access</a>
        </div>

        <div class="mt-10 pt-6 border-t border-line flex items-center gap-2">
          <span class="status-dot bg-signal-intel"></span>
          <span class="font-mono text-[10px] text-ink-dim">AES-256 · TLS 1.3 · Zero-trust session policy enforced</span>
        </div>
      </div>
    </div>
  `,
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);
  private router = inject(Router);

  loading = signal(false);
  error = signal<string | null>(null);

  form = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
  });

  nodes = Array.from({ length: 22 }, (_, i) => ({
    x: 40 + ((i * 137) % 720),
    y: 60 + ((i * 211) % 680),
    r: 3 + (i % 3),
    dur: 2 + (i % 4),
  }));

  links = Array.from({ length: 16 }, (_, i) => ({
    x1: 40 + ((i * 97) % 720),
    y1: 60 + ((i * 151) % 680),
    x2: 40 + ((i * 233) % 720),
    y2: 60 + ((i * 89) % 680),
  }));

  submit(): void {
    if (this.form.invalid) return;
    this.loading.set(true);
    this.error.set(null);
    this.auth.login(this.form.getRawValue()).subscribe({
      next: () => {
        this.loading.set(false);
        this.router.navigate(['/dashboard']);
      },
      error: () => {
        this.loading.set(false);
        this.error.set('Authentication failed. Verify your credentials and try again.');
      },
    });
  }
}
