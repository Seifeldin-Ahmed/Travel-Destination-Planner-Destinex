import { Injectable, computed, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthUser } from '../../models/auth.models';
import { decodeJwtPayload, isJwtExpired } from '../jwt';

const TOKEN_KEY = 'travel_jwt';

function extractTokenFromBody(body: unknown): string | null {
  if (!body || typeof body !== 'object') return null;
  const o = body as Record<string, unknown>;
  const t = o['token'] ?? o['accessToken'] ?? o['jwt'] ?? o['access_token'];
  return typeof t === 'string' ? t : null;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly tokenSig = signal<string | null>(this.readStoredToken());

  readonly token = this.tokenSig.asReadonly();
  readonly user = computed(() => this.parseUser(this.tokenSig()));

  constructor(
    private readonly http: HttpClient,
    private readonly router: Router
  ) {}

  private readStoredToken(): string | null {
    if (typeof localStorage === 'undefined') return null;
    const raw = localStorage.getItem(TOKEN_KEY);
    if (!raw) return null;
    const payload = decodeJwtPayload(raw);
    if (!payload || isJwtExpired(payload)) {
      localStorage.removeItem(TOKEN_KEY);
      return null;
    }
    return raw;
  }

  private parseUser(token: string | null): AuthUser | null {
    if (!token) return null;
    const payload = decodeJwtPayload(token);
    if (!payload || isJwtExpired(payload)) {
      return null;
    }
    const email = payload.email ?? payload.sub ?? '';
    const id = payload.id ?? 0;
    let roles = payload.roles ?? [];
    if (!roles.length && payload.role) {
      roles = [payload.role];
    }
    return { id, email, roles };
  }

  private clearSession(): void {
    localStorage.removeItem(TOKEN_KEY);
    this.tokenSig.set(null);
  }

  isAuthenticated(): boolean {
    const t = this.tokenSig();
    if (!t) return false;
    const payload = decodeJwtPayload(t);
    if (!payload || isJwtExpired(payload)) {
      this.clearSession();
      return false;
    }
    return true;
  }

  isAdmin(): boolean {
    const roles = this.user()?.roles ?? [];
    return roles.some((r) => /admin/i.test(r));
  }

  getToken(): string | null {
    const t = this.tokenSig();
    if (!t) return null;
    if (isJwtExpired(decodeJwtPayload(t))) {
      this.clearSession();
      return null;
    }
    return t;
  }

  async login(email: string, password: string): Promise<void> {
    const url = `${environment.apiUrl}${environment.loginPath}`;
    const body = await firstValueFrom(this.http.post<unknown>(url, { email, password }));
    const token = extractTokenFromBody(body);
    if (!token) {
      throw new Error('Login response did not include a token. Check loginPath and backend response shape.');
    }
    const p = decodeJwtPayload(token);
    if (isJwtExpired(p)) {
      throw new Error('Received an already-expired token from the server.');
    }
    this.setToken(token);
  }

  setToken(token: string): void {
    localStorage.setItem(TOKEN_KEY, token);
    this.tokenSig.set(token);
  }

  logout(): void {
    this.clearSession();
    void this.router.navigateByUrl('/');
  }

  /** After 401 or explicit expiry: clear storage and send user to login. */
  sessionExpiredRedirect(returnUrl?: string): void {
    this.clearSession();
    void this.router.navigate(['/login'], {
      queryParams:
        returnUrl && returnUrl !== '/' && returnUrl !== '/login'
          ? { returnUrl }
          : undefined,
    });
  }
}
