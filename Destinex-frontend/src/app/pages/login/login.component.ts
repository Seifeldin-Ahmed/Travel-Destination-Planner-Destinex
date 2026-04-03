import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { WantListService } from '../../core/services/want-list.service';

function loginErrorMessage(e: unknown): string {
  if (e instanceof HttpErrorResponse) {
    if (e.status === 401 || e.status === 403) {
      return 'Incorrect email or password. Please try again.';
    }
    const body = e.error;
    if (body && typeof body === 'object' && 'message' in body) {
      const m = (body as { message: unknown }).message;
      if (typeof m === 'string' && m.trim()) return m;
    }
    if (typeof body === 'string' && body.trim()) {
      try {
        const j = JSON.parse(body) as { message?: string };
        if (j.message?.trim()) return j.message;
      } catch {
        if (body.length < 400) return body;
      }
    }
    if (e.status === 400) return 'Invalid login request. Check your email and password.';
    if (e.status === 0) return 'Network error — is the backend running?';
    if (e.status >= 500) return 'The server could not process sign-in. Try again later.';
    return e.statusText || 'Sign-in failed.';
  }
  if (e instanceof Error) return e.message;
  return 'Sign-in failed.';
}

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly wantList = inject(WantListService);

  protected readonly error = signal<string | null>(null);
  protected readonly loading = signal(false);

  protected readonly form = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required],
  });

  async submit(): Promise<void> {
    this.error.set(null);
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.loading.set(true);
    try {
      const { email, password } = this.form.getRawValue();
      await this.auth.login(email, password);
      this.wantList.syncUser();
      const returnUrl = this.route.snapshot.queryParamMap.get('returnUrl') ?? '/destinations';
      await this.router.navigateByUrl(returnUrl);
    } catch (e: unknown) {
      this.error.set(loginErrorMessage(e));
    } finally {
      this.loading.set(false);
    }
  }
}
