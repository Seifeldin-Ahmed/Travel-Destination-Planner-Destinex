import { DecimalPipe } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DestinationService } from '../../../core/services/destination.service';
import { countryToDestinationRequest } from '../../../core/map-country';
import { CountryResponse, DestinationRequest } from '../../../models/destination.models';

@Component({
  selector: 'app-import-countries',
  standalone: true,
  imports: [DecimalPipe, FormsModule],
  templateUrl: './import-countries.component.html',
  styleUrl: './import-countries.component.css',
})
export class ImportCountriesComponent implements OnInit {
  private readonly api = inject(DestinationService);

  protected readonly rows = signal<CountryResponse[]>([]);
  protected readonly loading = signal(false);
  protected readonly error = signal<string | null>(null);
  protected readonly message = signal<string | null>(null);
  protected readonly busy = signal(false);
  protected readonly selected = signal<Set<string>>(new Set());
  /** Curated catalog filter — calls GET /admin/countries/{name} when non-empty. */
  protected catalogSearch = '';

  ngOnInit(): void {
    this.loadAllCountries();
  }

  /** Load entire curated suggestion list (GET /admin/countries). */
  refresh(): void {
    this.catalogSearch = '';
    this.loadAllCountries();
  }

  private loadAllCountries(): void {
    this.loading.set(true);
    this.error.set(null);
    this.api.getAdminCountries().subscribe({
      next: (list) => {
        this.rows.set(list ?? []);
        this.selected.set(new Set());
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Could not load suggestions. Check admin JWT and backend.');
        this.loading.set(false);
      },
    });
  }

  protected onCatalogSearchKeydown(ev: KeyboardEvent): void {
    if (ev.key !== 'Enter') return;
    ev.preventDefault();
    this.searchCatalog();
  }

  /** Uses GET /admin/countries/{name}; empty query reloads the full list. */
  searchCatalog(): void {
    const q = this.catalogSearch.trim();
    this.loading.set(true);
    this.error.set(null);
    if (!q) {
      this.loadAllCountries();
      return;
    }
    this.api.getAdminCountriesByName(q).subscribe({
      next: (list) => {
        this.rows.set(list ?? []);
        this.selected.set(new Set());
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Search failed. Check the country name and admin access.');
        this.loading.set(false);
      },
    });
  }

  protected rowKey(c: CountryResponse): string {
    return `${c.name}|${c.capital}`;
  }

  protected toggle(c: CountryResponse, checked: boolean): void {
    const key = this.rowKey(c);
    const next = new Set(this.selected());
    if (checked) next.add(key);
    else next.delete(key);
    this.selected.set(next);
  }

  protected isSelected(c: CountryResponse): boolean {
    return this.selected().has(this.rowKey(c));
  }

  protected onCheck(c: CountryResponse, ev: Event): void {
    const t = ev.target as HTMLInputElement | null;
    this.toggle(c, !!t?.checked);
  }

  protected saveOne(c: CountryResponse): void {
    const body = countryToDestinationRequest(c);
    this.postOne(body);
  }

  protected saveSelected(): void {
    const keys = this.selected();
    const list: DestinationRequest[] = [];
    for (const r of this.rows()) {
      if (keys.has(this.rowKey(r))) {
        list.push(countryToDestinationRequest(r));
      }
    }
    if (!list.length) {
      this.message.set('Select at least one country.');
      return;
    }
    this.busy.set(true);
    this.message.set(null);
    this.api.createDestinationsBulk(list).subscribe({
      next: (res) => {
        this.message.set(res.message ?? `Saved ${list.length} destination(s).`);
        this.busy.set(false);
        this.selected.set(new Set());
      },
      error: () => {
        this.message.set('Bulk save failed (validation or server error).');
        this.busy.set(false);
      },
    });
  }

  private postOne(body: DestinationRequest): void {
    this.busy.set(true);
    this.message.set(null);
    this.api.createDestination(body).subscribe({
      next: (res) => {
        this.message.set(res.message ?? `Saved ${body.name}.`);
        this.busy.set(false);
      },
      error: () => {
        this.message.set(`Could not save ${body.name}. Check flag URL and required fields.`);
        this.busy.set(false);
      },
    });
  }
}
