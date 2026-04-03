import { JwtPayload } from '../models/auth.models';

export function decodeJwtPayload(token: string): JwtPayload | null {
  try {
    const parts = token.split('.');
    if (parts.length < 2) return null;
    const base64 = parts[1].replace(/-/g, '+').replace(/_/g, '/');
    const json = decodeURIComponent(
      atob(base64)
        .split('')
        .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
        .join('')
    );
    return JSON.parse(json) as JwtPayload;
  } catch {
    return null;
  }
}

/** True if exp (Unix seconds) is missing, invalid, or in the past (with small skew). */
export function isJwtExpired(payload: JwtPayload | null, skewSeconds = 30): boolean {
  if (!payload || payload.exp == null) return true;
  const expMs = Number(payload.exp) * 1000;
  if (!Number.isFinite(expMs)) return true;
  return Date.now() >= expMs - skewSeconds * 1000;
}
