import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse, SignupRequest } from '../../models/destination.models';

@Injectable({ providedIn: 'root' })
export class SignupService {
  constructor(private readonly http: HttpClient) {}

  signup(body: SignupRequest): Observable<ApiResponse> {
    return this.http.post<ApiResponse>(`${environment.apiUrl}/signup`, body);
  }
}
