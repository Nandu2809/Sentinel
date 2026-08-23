import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { Role } from '../models/security.model';
import { AuthService } from '../services/auth.service';

export function roleGuard(allowed: Role[]): CanActivateFn {
  return () => {
    const auth = inject(AuthService);
    const router = inject(Router);
    const role = auth.role();

    if (role && allowed.includes(role)) {
      return true;
    }
    return router.createUrlTree(['/dashboard']);
  };
}
