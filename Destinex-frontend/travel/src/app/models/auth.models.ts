export interface JwtPayload {
  id?: number;
  email?: string;
  sub?: string;
  roles?: string[];
  role?: string;
  /** Unix timestamp (seconds) */
  exp?: number;
  iat?: number;
}

export interface AuthUser {
  id: number;
  email: string;
  roles: string[];
}
