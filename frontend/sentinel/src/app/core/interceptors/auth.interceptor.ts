import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, switchMap, throwError } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthService } from '../services/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const token = auth.getAccessToken();

  const isAuthEndpoint = req.url.includes('/auth/login') || req.url.includes('/auth/register');

  const authorizedReq = (token && !isAuthEndpoint && req.url.startsWith(environment.apiBaseUrl))
    ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
    : req;

  return next(authorizedReq).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401 && !isAuthEndpoint && !req.url.includes('/auth/refresh')) {
        return auth.refreshAccessToken().pipe(
          switchMap((res) => {
            const retriedReq = req.clone({ setHeaders: { Authorization: `Bearer ${res.accessToken}` } });
            return next(retriedReq);
          }),
          catchError((refreshError) => {
            auth.logout();
            router.navigate(['/login']);
            return throwError(() => refreshError);
          })
        );
      }
      return throwError(() => error);
    })
  );
};
