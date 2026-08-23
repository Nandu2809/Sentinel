import { Component, inject, signal } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'stn-register',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  template: `
    <div class="min-h-screen flex items-center justify-center bg-void-900 px-6">
      <div class="w-full max-w-sm">
        <div class="flex items-center gap-2 mb-8 justify-center">
          <div class="w-8 h-8 rounded-sm bg-signal-intel/15 border border-signal-intel/40 flex items-center justify-center">
            <span class="font-mono text-xs font-bold text-signal-intel">S</span>
          </div>
          <span class="font-mono text-sm font-semibold tracking-[0.25em] text-ink">SENTINEL</span>
        </div>

        <div class="bracket p-6">
          <div class="mono-label mb-2">Request Access</div>
          <h2 class="text-lg font-semibold text-ink mb-6">Create your analyst account</h2>

          <form [formGroup]="form" (ngSubmit)="submit()" class="space-y-4">
            <div>
              <label class="mono-label block mb-1.5">Full name</label>
              <input
                type="text" formControlName="name"
                class="w-full bg-void-800 border border-line rounded-sm px-3 py-2.5 text-sm text-ink focus:outline-none focus:ring-1 focus:ring-signal-live focus:border-signal-live"
                placeholder="Jane Doe"
              />
            </div>
            <div>
              <label class="mono-label block mb-1.5">Email</label>
              <input
                type="email" formControlName="email"
                class="w-full bg-void-800 border border-line rounded-sm px-3 py-2.5 text-sm text-ink focus:outline-none focus:ring-1 focus:ring-signal-live focus:border-signal-live"
                placeholder="analyst@organization.com"
              />
            </div>
            <div>
              <label class="mono-label block mb-1.5">Password</label>
              <input
                type="password" formControlName="password"
                class="w-full bg-void-800 border border-line rounded-sm px-3 py-2.5 text-sm text-ink focus:outline-none focus:ring-1 focus:ring-signal-live focus:border-signal-live"
                placeholder="••••••••••••"
              />
            </div>

            <button
              type="submit"
              [disabled]="form.invalid || loading()"
              class="w-full bg-signal-live/10 hover:bg-signal-live/20 disabled:opacity-40 border border-signal-live/40 text-signal-live font-mono text-xs uppercase tracking-wider py-3 rounded-sm transition-colors"
            >
              {{ loading() ? 'Provisioning…' : 'Create account' }}
            </button>
          </form>

          <div class="mt-6 pt-4 border-t border-line text-center text-xs text-ink-muted">
            Already have access?
            <a routerLink="/login" class="text-signal-live hover:underline">Sign in</a>
          </div>
        </div>
      </div>
    </div>
  `,
})
export class RegisterComponent {
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);
  private router = inject(Router);

  loading = signal(false);

  form = this.fb.nonNullable.group({
    name: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
  });

  submit(): void {
    if (this.form.invalid) return;
    this.loading.set(true);
    this.auth.register(this.form.getRawValue()).subscribe(() => {
      this.loading.set(false);
      this.router.navigate(['/dashboard']);
    });
  }
}
