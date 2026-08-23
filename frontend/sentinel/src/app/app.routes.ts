import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
  {
    path: 'login',
    loadComponent: () => import('./features/auth/login/login.component').then((m) => m.LoginComponent),
    title: 'Sentinel — Secure Access',
  },
  {
    path: 'register',
    loadComponent: () => import('./features/auth/register/register.component').then((m) => m.RegisterComponent),
    title: 'Sentinel — Register',
  },
  {
    path: '',
    loadComponent: () => import('./layout/shell.component').then((m) => m.ShellComponent),
    canActivate: [authGuard],
    children: [
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./features/dashboard/dashboard.component').then((m) => m.DashboardComponent),
        title: 'Sentinel — Security Operations',
      },
      {
        path: 'alerts',
        loadComponent: () => import('./features/alerts/alerts.component').then((m) => m.AlertsComponent),
        title: 'Sentinel — Alert Management',
      },
      {
        path: 'threats',
        loadComponent: () => import('./features/threats/threats.component').then((m) => m.ThreatsComponent),
        title: 'Sentinel — Threat Analysis',
      },
      {
        path: 'risk',
        loadComponent: () => import('./features/risk/risk.component').then((m) => m.RiskComponent),
        title: 'Sentinel — Risk Intelligence',
      },
      {
        path: 'profile',
        loadComponent: () => import('./features/profile/profile.component').then((m) => m.ProfileComponent),
        title: 'Sentinel — Security Profile',
      },
      {
        path: 'admin',
        loadComponent: () => import('./features/admin/admin.component').then((m) => m.AdminComponent),
        canActivate: [roleGuard(['ADMIN'])],
        title: 'Sentinel — Admin Control Center',
      },
    ],
  },
  { path: '**', redirectTo: 'dashboard' },
];
