import { Routes } from '@angular/router';
import { authGuard, roleGuard } from './auth/auth.guard';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'animals',
    pathMatch: 'full',
  },
  {
    path: 'animals',
    canActivate: [authGuard],
    loadComponent: () => import('./app').then(m => m.App),
  },
  {
    path: 'health-records',
    canActivate: [roleGuard('view-health', 'manage-health')],
    loadComponent: () => import('./app').then(m => m.App),
  },
  {
    path: 'finances',
    canActivate: [roleGuard('view-finances', 'manage-finances')],
    loadComponent: () => import('./app').then(m => m.App),
  },
  {
    path: 'admin',
    canActivate: [roleGuard('manage-animal', 'manage-health', 'manage-finances')],
    loadComponent: () => import('./app').then(m => m.App),
  },
  {
    path: 'unauthorized',
    loadComponent: () => import('./app').then(m => m.App),
  },
];
