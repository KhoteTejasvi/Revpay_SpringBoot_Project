import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DashboardService {

  private baseUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  getSummary(): Observable<any> {
    return this.http.get(`${this.baseUrl}/dashboard/summary`);
  }

  getBalance(): Observable<number> {
    return this.http.get<number>(`${this.baseUrl}/wallet/balance`);
  }

  getTransactions(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/wallet/transactions`);
  }

  getTopCustomers(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/analytics/top-customers`);
  }

  getRevenueChart(type: string): Observable<any> {
    return this.http.get(`${this.baseUrl}/analytics/revenue/${type}`);
  }
}
