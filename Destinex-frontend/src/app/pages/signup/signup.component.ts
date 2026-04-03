import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  ReactiveFormsModule,
  ValidationErrors,
  ValidatorFn,
  Validators,
} from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import { SignupService } from '../../core/services/signup.service';

const matchPasswords: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
  const p = control.get('password')?.value;
  const c = control.get('confirmPassword')?.value;
  if (p && c && p !== c) return { mismatch: true };
  return null;
};

function signupErrorMessage(e: unknown): string {
  if (e instanceof HttpErrorResponse) {
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
    if (e.status === 0) return 'Network error — is the backend running?';
    if (e.status === 400) return 'Could not create this account. Please check your details.';
    if (e.status === 409) return 'An account with this email may already exist.';
    if (e.status >= 500) return 'The server is busy. Please try again in a moment.';
    return e.statusText || 'Signup failed.';
  }
  return 'Signup failed.';
}

@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './signup.component.html',
  styleUrl: './signup.component.css',
})
export class SignupComponent {
  private readonly fb = inject(FormBuilder);
  private readonly signupApi = inject(SignupService);
  private readonly router = inject(Router);

  protected readonly error = signal<string | null>(null);
  protected readonly loading = signal(false);
  /** True after user attempts submit — drives visible field errors. */
  protected readonly submitted = signal(false);

  protected readonly form = this.fb.nonNullable.group(
    {
      email: ['', [Validators.required, Validators.email]],
      password: [
        '',
        [Validators.required, Validators.minLength(6), Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z]).{6,}$/)],
      ],
      confirmPassword: ['', Validators.required],
      firstName: ['', [Validators.required, Validators.minLength(2)]],
      lastName: ['', [Validators.required, Validators.minLength(2)]],
      address: [''],
      phoneNumber: [''],
    },
    { validators: matchPasswords }
  );

  protected isFieldInvalid(controlName: string): boolean {
    const c = this.form.get(controlName);
    return !!c && c.invalid && (this.submitted() || c.touched || c.dirty);
  }

  protected fieldHint(controlName: string): string | null {
    const c = this.form.get(controlName);
    if (!c || !c.errors || !this.isFieldInvalid(controlName)) return null;
    const e = c.errors;
    if (e['required']) return 'This field is required.';
    if (e['email']) return 'Enter a valid email address.';
    if (e['minlength']) {
      const min = (e['minlength'] as { requiredLength?: number }).requiredLength;
      return min ? `Use at least ${min} characters.` : 'Too short.';
    }
    if (e['pattern'] && controlName === 'password') {
      return 'Use 6+ characters with at least one uppercase and one lowercase letter.';
    }
    if (e['invalidPhone']) return 'Use exactly 11 digits, or leave this blank.';
    return null;
  }

  protected passwordMismatchHint(): string | null {
    if (!this.submitted()) return null;
    if (!this.form.errors?.['mismatch']) return null;
    return 'Passwords must match.';
  }

  protected validationSummary(): string | null {
    if (!this.submitted() || !this.form.invalid || this.loading()) return null;
    if (this.form.pending) return null;
    return 'Please fix the highlighted fields before continuing.';
  }

  submit(): void {
    this.error.set(null);
    this.submitted.set(true);

    const phoneCtrl = this.form.get('phoneNumber');
    phoneCtrl?.setErrors(null);
    const raw = this.form.getRawValue();
    const phone = raw.phoneNumber?.replace(/\D/g, '') ?? '';
    if (phoneCtrl?.value?.trim() && phone.length !== 11) {
      phoneCtrl.setErrors({ invalidPhone: true });
    }

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      queueMicrotask(() => this.scrollToFirstInvalid());
      return;
    }

    const v = this.form.getRawValue();
    const phoneClean = v.phoneNumber?.replace(/\D/g, '') ?? '';
    this.loading.set(true);
    this.signupApi
      .signup({
        email: v.email.trim(),
        password: v.password,
        confirmPassword: v.confirmPassword,
        firstName: v.firstName.trim(),
        lastName: v.lastName.trim(),
        role: 'CUSTOMER',
        ...(v.address?.trim() ? { address: v.address.trim() } : {}),
        ...(phoneClean.length === 11 ? { phoneNumber: phoneClean } : {}),
      })
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: () => {
          void this.router.navigate(['/login'], {
            queryParams: { registered: '1' },
          });
        },
        error: (e: unknown) => {
          this.error.set(signupErrorMessage(e));
        },
      });
  }

  private scrollToFirstInvalid(): void {
    const el = document.querySelector('.signup-form .field.is-invalid');
    el?.scrollIntoView({ behavior: 'smooth', block: 'center' });
  }
}
