import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { KeycloakService } from './keycloak.service';

export const authGuard: CanActivateFn = async () => {
  const keycloak = inject(KeycloakService);
  const router = inject(Router);

  if (keycloak.isAuthenticated) {
    return true;
  }

  try {
    await keycloak.init();
    return keycloak.isAuthenticated || router.createUrlTree(['/unauthorized']);
  } catch {
    return router.createUrlTree(['/unauthorized']);
  }
};

export const roleGuard = (...requiredRoles: string[]): CanActivateFn => {
  return async () => {
    const keycloak = inject(KeycloakService);
    const router = inject(Router);

    if (!keycloak.isAuthenticated) {
      try {
        await keycloak.init();
      } catch {
        return router.createUrlTree(['/unauthorized']);
      }
    }

    if (keycloak.hasAnyRole(...requiredRoles)) {
      return true;
    }

    return router.createUrlTree(['/unauthorized']);
  };
};
