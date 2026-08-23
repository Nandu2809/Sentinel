import { HttpClient } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { Observable, catchError, map, of, tap, throwError } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthResponse, AuthUser, LoginRequest, Role } from '../models/security.model';

const ACCESS_TOKEN_KEY = 'sentinel_access_token';
const REFRESH_TOKEN_KEY = 'sentinel_refresh_token';
const USER_KEY = 'sentinel_user';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly _user = signal<AuthUser | null>(null);

  readonly user = this._user.asReadonly();
  readonly isAuthenticated = computed(() => this._user() !== null);
  readonly role = computed<Role | null>(() => this._user()?.role ?? null);

  constructor() {
    this.restoreSession();
  }

  /**
   * POST /api/v1/auth/login via API Gateway
   */
  login(request: LoginRequest): Observable<AuthResponse> {
    return this.http.post<any>(`${environment.apiBaseUrl}/auth/login`, request).pipe(
      map((res) => this.normalizeAuthResponse(res, request.email)),
      tap((res) => this.persistSession(res)),
      catchError((error) => {
        // Fallback for development if offline/mock fallback required
        console.warn('Backend login request failed, checking mock fallback if dev mode:', error);
        return throwError(() => error);
      })
    );
  }

  /**
   * POST /api/v1/auth/register via API Gateway
   */
  register(payload: { name: string; email: string; password: string }): Observable<AuthResponse> {
    const names = (payload.name || 'Sentinel User').split(' ');
    const firstName = names[0] || 'Sentinel';
    const lastName = names.slice(1).join(' ') || 'User';
    const username = payload.email.split('@')[0] || 'user' + Math.floor(Math.random() * 1000);

    const backendPayload = {
      username,
      firstName,
      lastName,
      email: payload.email,
      password: payload.password,
      confirmPassword: payload.password
    };

    return this.http.post<any>(`${environment.apiBaseUrl}/auth/register`, backendPayload).pipe(
      map((res) => this.normalizeAuthResponse(res, payload.email)),
      tap((res) => this.persistSession(res))
    );
  }

  /**
   * POST /api/v1/auth/refresh via API Gateway
   */
  refreshAccessToken(): Observable<{ accessToken: string }> {
    const refreshToken = localStorage.getItem(REFRESH_TOKEN_KEY);
    if (!refreshToken) {
      return throwError(() => new Error('No refresh token available'));
    }

    return this.http.post<any>(`${environment.apiBaseUrl}/auth/refresh`, { refreshToken }).pipe(
      map((res) => {
        const data = res?.data || res;
        const newAccessToken = data?.accessToken || data?.token;
        if (newAccessToken) {
          localStorage.setItem(ACCESS_TOKEN_KEY, newAccessToken);
          if (data?.refreshToken) {
            localStorage.setItem(REFRESH_TOKEN_KEY, data.refreshToken);
          }
          return { accessToken: newAccessToken };
        }
        throw new Error('Refresh token failed');
      })
    );
  }

  logout(): void {
    localStorage.removeItem(ACCESS_TOKEN_KEY);
    localStorage.removeItem(REFRESH_TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    this._user.set(null);
  }

  getAccessToken(): string | null {
    return localStorage.getItem(ACCESS_TOKEN_KEY);
  }

  restoreSession(): void {
    const token = this.getAccessToken();
    const storedUser = localStorage.getItem(USER_KEY);
    if (token && storedUser) {
      try {
        const parsedUser: AuthUser = JSON.parse(storedUser);
        this._user.set(parsedUser);
      } catch {
        this.logout();
      }
    }
  }

  private normalizeAuthResponse(res: any, fallbackEmail: string): AuthResponse {
    const data = res?.data || res;
    const accessToken = data?.accessToken || data?.token || '';
    const refreshToken = data?.refreshToken || '';
    const userObj = data?.user || {};

    const rawRole = typeof userObj.role === 'object' ? userObj.role?.name : userObj.role;
    let role: Role = 'USER';
    if (rawRole === 'ADMIN') role = 'ADMIN';
    else if (rawRole === 'SECURITY_ANALYST' || rawRole === 'ANALYST') role = 'SECURITY_ANALYST';

    const name = userObj.name ||
      (userObj.firstName ? `${userObj.firstName} ${userObj.lastName || ''}`.trim() : userObj.username) ||
      'Sentinel User';

    const user: AuthUser = {
      id: userObj.id || crypto.randomUUID(),
      name,
      email: userObj.email || fallbackEmail,
      role,
      riskScore: userObj.riskScore ?? 15,
    };

    return {
      accessToken,
      refreshToken,
      user,
    };
  }

  private persistSession(res: AuthResponse): void {
    if (res.accessToken) {
      localStorage.setItem(ACCESS_TOKEN_KEY, res.accessToken);
    }
    if (res.refreshToken) {
      localStorage.setItem(REFRESH_TOKEN_KEY, res.refreshToken);
    }
    if (res.user) {
      localStorage.setItem(USER_KEY, JSON.stringify(res.user));
      this._user.set(res.user);
    }
  }
}
