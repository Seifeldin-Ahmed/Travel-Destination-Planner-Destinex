import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import {
  ApiResponse,
  CountryResponse,
  Destination,
  DestinationRequest,
  PageResponse,
} from '../../models/destination.models';
import {
  normalizeDestination,
  normalizeDestinationList,
  normalizePageResponse,
} from '../normalize-destination';

@Injectable({ providedIn: 'root' })
export class DestinationService {
  private readonly base = environment.apiUrl;

  constructor(private readonly http: HttpClient) {}

  getDestinationsPage(page: number): Observable<PageResponse> {
    const params = new HttpParams().set('page', String(page));
    return this.http
      .get<unknown>(`${this.base}/destination`, { params })
      .pipe(map((raw) => normalizePageResponse(raw)));
  }

  getDestination(id: number): Observable<Destination> {
    return this.http
      .get<unknown>(`${this.base}/destination/${id}`)
      .pipe(map((raw) => normalizeDestination(raw)));
  }

  /** Search by country name (backend path param). */
  getByCountryName(countryName: string): Observable<Destination[]> {
    const q = countryName.trim();
    const enc = encodeURIComponent(q);
    return this.http
      .get<unknown>(`${this.base}/destination/countryName/${enc}`)
      .pipe(map((raw) => normalizeDestinationList(raw)));
  }

  getWishlist(): Observable<Destination[]> {
    return this.http
      .get<unknown>(`${this.base}/destination/wishlist`)
      .pipe(map((raw) => normalizeDestinationList(raw)));
  }

  addToWishlist(destinationId: number): Observable<unknown> {
    return this.http.post(`${this.base}/destination/wishlist/${destinationId}`, {});
  }

  removeFromWishlist(destinationId: number): Observable<unknown> {
    return this.http.delete(`${this.base}/destination/wishlist/${destinationId}`);
  }

  clearWishlist(): Observable<unknown> {
    return this.http.delete(`${this.base}/destination/wishlist`);
  }

  getAdminCountries(): Observable<CountryResponse[]> {
    return this.http.get<CountryResponse[]>(`${this.base}/admin/countries`);
  }

  /** Filter curated country suggestions by name (path segment; encoded for URL safety). */
  getAdminCountriesByName(name: string): Observable<CountryResponse[]> {
    const enc = encodeURIComponent(name.trim());
    return this.http.get<CountryResponse[]>(`${this.base}/admin/countries/${enc}`);
  }

  createDestination(body: DestinationRequest): Observable<ApiResponse> {
    return this.http.post<ApiResponse>(`${this.base}/admin/destination/country`, body);
  }

  createDestinationsBulk(list: DestinationRequest[]): Observable<ApiResponse> {
    return this.http.post<ApiResponse>(`${this.base}/admin/destination/countries`, list);
  }

  updateDestination(id: number, body: DestinationRequest): Observable<ApiResponse> {
    return this.http.put<ApiResponse>(`${this.base}/admin/destination/${id}`, body);
  }

  deleteDestination(id: number): Observable<ApiResponse> {
    return this.http.delete<ApiResponse>(`${this.base}/admin/destination/${id}`);
  }
}
