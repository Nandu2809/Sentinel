import { Injectable, computed, signal } from '@angular/core';
import { Observable, delay, of, tap } from 'rxjs';
import { AuthResponse, AuthUser, LoginRequest, Role } from '../models/security.model';

const ACCESS_TOKEN_KEY = 'sentinel_access_token';
const REFRESH_TOKEN_KEY = 'sentinel_refresh_token';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly _user = signal<AuthUser | null>(null);
  readonly user = this._user.asReadonly();
  readonly isAuthenticated = computed(() => this._user() !== null);
  readonly role = computed<Role | null>(() => this._user()?.role ?? null);

  /**
   * POST /api/v1/auth/login
   * Placeholder simulates network latency + backend contract until wired to Sentinel's API gateway.
   */
  login(request: LoginRequest): Observable<AuthResponse> {
    const mockUser: AuthUser = {
      id: 'usr-2460402',
      name: 'Nanda Kishore',
      email: request.email,
      role: 'SECURITY_ANALYST',
      riskScore: 18,
    };
    const response: AuthResponse = {
      accessToken: 'mock.jwt.access',
      refreshToken: 'mock.jwt.refresh',
      user: mockUser,
    };
    return of(response).pipe(
      delay(650),
      tap((res) => this.persistSession(res)),
    );
  }

  /**
   * POST /api/v1/auth/register
   */
  register(payload: { name: string; email: string; password: string }): Observable<AuthResponse> {
    const mockUser: AuthUser = {
      id: crypto.randomUUID(),
      name: payload.name,
      email: payload.email,
      role: 'USER',
      riskScore: 5,
    };
    const response: AuthResponse = {
      accessToken: 'mock.jwt.access',
      refreshToken: 'mock.jwt.refresh',
      user: mockUser,
    };
    return of(response).pipe(
      delay(650),
      tap((res) => this.persistSession(res)),
    );
  }

  refreshAccessToken(): Observable<{ accessToken: string }> {
    return of({ accessToken: 'mock.jwt.access.refreshed' }).pipe(delay(200));
  }

  logout(): void {
    localStorage.removeItem(ACCESS_TOKEN_KEY);
    localStorage.removeItem(REFRESH_TOKEN_KEY);
    this._user.set(null);
  }

  getAccessToken(): string | null {
    return localStorage.getItem(ACCESS_TOKEN_KEY);
  }

  restoreSession(): void {
    const token = this.getAccessToken();
    if (token && !this._user()) {
      this._user.set({
        id: 'usr-2460402',
        name: 'Nanda Kishore',
        email: 'n.kishore@sentinel.io',
        role: 'SECURITY_ANALYST',
        riskScore: 18,
      });
    }
  }

  private persistSession(res: AuthResponse): void {
    localStorage.setItem(ACCESS_TOKEN_KEY, res.accessToken);
    localStorage.setItem(REFRESH_TOKEN_KEY, res.refreshToken);
    this._user.set(res.user);
  }
}
