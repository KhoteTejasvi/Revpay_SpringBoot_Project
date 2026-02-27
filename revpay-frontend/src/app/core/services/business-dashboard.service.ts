import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class BusinessDashboardService {

  private baseUrl = 'http://localhost:8080/api/dashboard';

  constructor(
    private http: HttpClient,
    private auth: AuthService
  ) {}

  private headers() {
    const token = this.auth.getToken();

    return {
      headers: new HttpHeaders({
        Authorization: `Bearer ${token}`
      })
    };
  }

  getDashboard() {
    return this.http.get<any>(this.baseUrl, this.headers());
  }
}
