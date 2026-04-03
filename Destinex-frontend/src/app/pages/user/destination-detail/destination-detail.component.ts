import { DecimalPipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { catchError, finalize, map, of, switchMap } from 'rxjs';
import { AuthService } from '../../../core/services/auth.service';
import { DestinationService } from '../../../core/services/destination.service';
import { WantListService } from '../../../core/services/want-list.service';
import { Destination, DestinationRequest } from '../../../models/destination.models';

@Component({
  selector: 'app-destination-detail',
  standalone: true,
  imports: [RouterLink, DecimalPipe, ReactiveFormsModule],
  templateUrl: './destination-detail.component.html',
  styleUrl: './destination-detail.component.css',
})
export class DestinationDetailComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly api = inject(DestinationService);
  private readonly auth = inject(AuthService);
  private readonly fb = inject(FormBuilder);
  protected readonly wantList = inject(WantListService);

  /** Preserves list page when returning from a destination opened from paginated browse. */
  protected readonly returnPageFromList = toSignal(
    this.route.queryParamMap.pipe(map((q) => q.get('returnPage'))),
    { initialValue: this.route.snapshot.queryParamMap.get('returnPage') }
  );

  protected readonly loading = signal(true);
  protected readonly error = signal<string | null>(null);
  protected readonly destination = signal<Destination | null>(null);
  protected readonly message = signal<string | null>(null);
  protected readonly heroImageBroken = signal(false);
  protected busy = false;
  protected editing = false;

  protected readonly isAdmin = () => this.auth.isAdmin();

  protected exploreBackQueryParams(): Record<string, string> {
    const p = this.returnPageFromList();
    if (p != null && p !== '') return { page: p };
    return {};
  }

  protected readonly form = this.fb.nonNullable.group({
    name: ['', [Validators.required, Validators.minLength(1)]],
    capital: ['', Validators.required],
    region: ['', Validators.required],
    population: [1, [Validators.required, Validators.min(1)]],
    currency: ['', Validators.required],
    flag: [''],
  });

  constructor() {
    this.route.paramMap
      .pipe(
        map((pm) => Number(pm.get('id'))),
        switchMap((id) => {
          this.heroImageBroken.set(false);
          this.loading.set(true);
          this.error.set(null);
          this.destination.set(null);
          this.message.set(null);
          this.editing = false;
          if (!Number.isFinite(id) || id <= 0) {
            this.loading.set(false);
            this.error.set('Invalid destination.');
            return of(null);
          }
          return this.api.getDestination(id).pipe(
            catchError((err: unknown) => {
              if (err instanceof HttpErrorResponse) {
                const msg = httpErrMessage(err);
                this.error.set(msg);
              } else {
                this.error.set('Could not load this destination.');
              }
              return of(null);
            }),
            finalize(() => this.loading.set(false))
          );
        }),
        takeUntilDestroyed()
      )
      .subscribe((d) => {
        this.destination.set(d);
        if (d) {
          const ok = !!(d.country?.trim() || d.capital?.trim() || d.region?.trim());
          if (!ok) {
            this.error.set('The server returned data we could not display for this place.');
          } else {
            this.error.set(null);
          }
        } else if (!this.error()) {
          this.error.set('Destination not found.');
        }
      });
  }

  protected openEdit(): void {
    const d = this.destination();
    if (!d) return;
    this.editing = true;
    this.form.patchValue({
      name: d.country,
      capital: d.capital,
      region: d.region,
      population: d.population,
      currency: d.currency,
      flag: d.imageUrl ?? '',
    });
  }

  protected closeEdit(): void {
    this.editing = false;
  }

  protected saveEdit(): void {
    const d = this.destination();
    if (!d || this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const v = this.form.getRawValue();
    const body: DestinationRequest = {
      name: v.name,
      capital: v.capital,
      region: v.region,
      population: v.population,
      currency: v.currency,
      ...(v.flag?.trim() ? { flag: v.flag.trim() } : {}),
    };
    this.busy = true;
    this.api.updateDestination(d.id, body).subscribe({
      next: (res) => {
        this.message.set(res.message ?? 'Updated.');
        this.busy = false;
        this.editing = false;
        this.api.getDestination(d.id).subscribe((fresh) => {
          this.destination.set(fresh);
        });
      },
      error: () => {
        this.message.set('Update failed.');
        this.busy = false;
      },
    });
  }

  protected deleteDestination(): void {
    const d = this.destination();
    if (!d) return;
    if (!confirm(`Remove ${d.country} from the catalog?`)) return;
    this.busy = true;
    this.api.deleteDestination(d.id).subscribe({
      next: () => {
        void this.router.navigate(['/destinations'], { queryParams: this.exploreBackQueryParams() });
      },
      error: () => {
        this.message.set('Delete failed.');
        this.busy = false;
      },
    });
  }

  protected async toggleWant(): Promise<void> {
    const id = this.destination()?.id;
    if (id == null) return;
    try {
      await this.wantList.toggle(id);
    } catch {
      this.message.set('Could not update wishlist.');
    }
  }

  protected wants(): boolean {
    const id = this.destination()?.id;
    if (id == null) return false;
    return this.wantList.wants(id);
  }

  protected onFlagError(): void {
    this.heroImageBroken.set(true);
  }
}

function httpErrMessage(e: HttpErrorResponse): string {
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
      if (body.length < 300) return body;
    }
  }
  if (e.status === 404) return 'This destination could not be found.';
  if (e.status === 401 || e.status === 403) return 'You are not allowed to view this destination.';
  if (e.status === 0) return 'Network error — is the backend running?';
  return e.statusText || 'Something went wrong.';
}
