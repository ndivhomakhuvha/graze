import { Injectable } from '@angular/core';
import Keycloak from 'keycloak-js';
import { environment } from '../../environments/environment';

export const keycloakInstance = new Keycloak(environment.keycloak);

@Injectable({ providedIn: 'root' })
export class KeycloakService {
  private _keycloak = keycloakInstance;

  get keycloak(): Keycloak {
    return this._keycloak;
  }

  async init(): Promise<boolean> {
    return this._keycloak.init({
      onLoad: 'login-required',
      checkLoginIframe: false,
    });
  }

  get token(): string | undefined {
    return this._keycloak.token;
  }

  get isAuthenticated(): boolean {
    return !!this._keycloak.authenticated;
  }

  get username(): string | undefined {
    return this._keycloak.tokenParsed?.['preferred_username'];
  }

  get roles(): string[] {
    return this._keycloak.tokenParsed?.['roles'] ?? [];
  }

  hasRole(role: string): boolean {
    return this.roles.includes(role);
  }

  hasAnyRole(...roles: string[]): boolean {
    return roles.some(role => this.hasRole(role));
  }

  async refreshToken(): Promise<boolean> {
    return this._keycloak.updateToken(30);
  }

  logout(): void {
    this._keycloak.logout({ redirectUri: window.location.origin });
  }
}
