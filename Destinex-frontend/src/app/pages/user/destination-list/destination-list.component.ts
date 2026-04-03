import { DecimalPipe } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { DestinationService } from '../../../core/services/destination.service';
import { WantListService } from '../../../core/services/want-list.service';
import { Destination, DestinationRequest } from '../../../models/destination.models';

@Component({
  selector: 'app-destination-list',
  standalone: true,
  imports: [FormsModule, ReactiveFormsModule, RouterLink, DecimalPipe],
  templateUrl: './destination-list.component.html',
  styleUrl: './destination-list.component.css',
})
export class DestinationListComponent implements OnInit {
  private readonly api = inject(DestinationService);
  private readonly auth = inject(AuthService);
  private readonly fb = inject(FormBuilder);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  protected readonly wantList = inject(WantListService);

  protected readonly page = signal(0);
  protected readonly loading = signal(false);
  protected readonly error = signal<string | null>(null);
  protected readonly message = signal<string | null>(null);
  protected readonly items = signal<Destination[]>([]);
  protected readonly totalElements = signal(0);
  protected readonly elementsPerPage = signal(0);
  protected readonly searchMode = signal<'browse' | 'search'>('browse');
  protected countrySearch = '';
  protected readonly busy = signal(false);
  protected editing = signal<Destination | null>(null);

  protected readonly form = this.fb.nonNullable.group({
    name: ['', [Validators.required, Validators.minLength(1)]],
    capital: ['', Validators.required],
    region: ['', Validators.required],
    population: [1, [Validators.required, Validators.min(1)]],
    currency: ['', Validators.required],
    flag: [''],
  });

  protected readonly isAdmin = () => this.auth.isAdmin();

  ngOnInit(): void {
    const pageStr = this.route.snapshot.queryParamMap.get('page');
    if (pageStr != null) {
      const p = Number.parseInt(pageStr, 10);
      if (Number.isFinite(p) && p >= 0) this.page.set(p);
    }
    this.loadBrowse();
  }

  /** Pass when opening detail from paginated browse so “back” restores the same page. */
  protected detailReturnQuery(): Record<string, string> {
    if (this.searchMode() !== 'browse') return {};
    return { returnPage: String(this.page()) };
  }

  private syncBrowsePageToUrl(): void {
    void this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { page: this.page() },
      queryParamsHandling: 'merge',
      replaceUrl: true,
    });
  }

  private dropPageQueryFromUrl(): void {
    void this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { page: null },
      queryParamsHandling: 'merge',
      replaceUrl: true,
    });
  }

  protected loadBrowse(): void {
    this.searchMode.set('browse');
    this.loading.set(true);
    this.error.set(null);
    this.api.getDestinationsPage(this.page()).subscribe({
      next: (res) => {
        this.items.set(res.content ?? []);
        this.totalElements.set(res.totalElements ?? 0);
        this.elementsPerPage.set(res.elementsPerPage ?? Math.max(1, res.content?.length ?? 0));
        this.syncBrowsePageToUrl();
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Could not load destinations. Is the backend running?');
        this.loading.set(false);
      },
    });
  }

  protected onSearchKeydown(ev: KeyboardEvent): void {
    if (ev.key !== 'Enter') return;
    ev.preventDefault();
    const q = this.countrySearch.trim();
    if (!q) {
      this.page.set(0);
      this.loadBrowse();
      return;
    }
    this.runCountrySearch(q);
  }

  protected clearSearch(): void {
    this.countrySearch = '';
    this.page.set(0);
    this.loadBrowse();
  }

  private runCountrySearch(q: string): void {
    this.searchMode.set('search');
    this.loading.set(true);
    this.error.set(null);
    this.dropPageQueryFromUrl();
    this.api.getByCountryName(q).subscribe({
      next: (list) => {
        this.items.set(list);
        this.totalElements.set(list.length);
        this.elementsPerPage.set(Math.max(1, list.length || 1));
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Search failed. Try another country name.');
        this.loading.set(false);
      },
    });
  }

  protected prev(): void {
    if (!this.canPrev()) return;
    this.page.update((p) => p - 1);
    this.loadBrowse();
  }

  protected next(): void {
    if (!this.canNext()) return;
    this.page.update((p) => p + 1);
    this.loadBrowse();
  }

  protected canNext(): boolean {
    if (this.searchMode() === 'search') return false;
    const total = this.totalElements();
    const per = Math.max(1, this.elementsPerPage());
    const lastPage = Math.max(0, Math.ceil(total / per) - 1);
    return this.page() < lastPage;
  }

  protected canPrev(): boolean {
    if (this.searchMode() === 'search') return false;
    return this.page() > 0;
  }

  protected pageLabel(): string {
    if (this.searchMode() === 'search') {
      return `${this.items().length} result(s) for your search`;
    }
    const total = this.totalElements();
    const per = Math.max(1, this.elementsPerPage());
    const last = Math.max(0, Math.ceil(total / per) - 1);
    return `Page ${this.page() + 1} of ${last + 1} (${total} total)`;
  }

  protected async toggleWant(id: number, ev: Event): Promise<void> {
    ev.preventDefault();
    ev.stopPropagation();
    try {
      await this.wantList.toggle(id);
    } catch {
      this.message.set('Could not update wishlist.');
    }
  }

  protected wants(id: number): boolean {
    return this.wantList.wants(id);
  }

  protected openEdit(d: Destination): void {
    this.editing.set(d);
    this.form.patchValue({
      name: d.country,
      capital: d.capital,
      region: d.region,
      population: d.population,
      currency: d.currency,
      flag: d.imageUrl ?? '',
    });
    this.message.set(null);
  }

  protected closeEdit(): void {
    this.editing.set(null);
  }

  protected saveEdit(): void {
    const d = this.editing();
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
    this.busy.set(true);
    this.api.updateDestination(d.id, body).subscribe({
      next: (res) => {
        this.message.set(res.message ?? 'Updated.');
        this.busy.set(false);
        this.closeEdit();
        if (this.searchMode() === 'browse') this.loadBrowse();
        else this.runCountrySearch(this.countrySearch.trim());
      },
      error: () => {
        this.message.set('Update failed.');
        this.busy.set(false);
      },
    });
  }

  protected deleteRow(d: Destination, ev: Event): void {
    ev.preventDefault();
    ev.stopPropagation();
    if (!confirm(`Remove ${d.country} from the catalog?`)) return;
    this.busy.set(true);
    this.api.deleteDestination(d.id).subscribe({
      next: (res) => {
        this.message.set(res.message ?? 'Deleted.');
        this.busy.set(false);
        if (this.searchMode() === 'browse') this.loadBrowse();
        else this.runCountrySearch(this.countrySearch.trim());
      },
      error: () => {
        this.message.set('Delete failed.');
        this.busy.set(false);
      },
    });
  }
}
