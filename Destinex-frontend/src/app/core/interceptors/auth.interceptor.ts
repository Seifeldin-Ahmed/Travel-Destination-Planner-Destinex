import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthService } from '../services/auth.service';

function isPublicAuthUrl(url: string): boolean {
  if (url.includes('/signup')) return true;
  if (url.includes(environment.loginPath)) return true;
  return false;
}

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const token = auth.getToken();
  const out =
    token && !isPublicAuthUrl(req.url)
      ? next(
          req.clone({
            setHeaders: { Authorization: `Bearer ${token}` },
          })
        )
      : next(req);
  return out.pipe(
    catchError((err: unknown) => {
      if (err instanceof HttpErrorResponse && err.status === 401 && !isPublicAuthUrl(req.url)) {
        auth.sessionExpiredRedirect(router.url);
      }
      return throwError(() => err);
    })
  );
};
