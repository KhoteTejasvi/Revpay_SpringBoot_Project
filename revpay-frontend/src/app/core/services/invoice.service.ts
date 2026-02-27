import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class InvoiceService {

  private baseUrl = 'http://localhost:8080/api/invoice';

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

  getMyInvoices() {
    return this.http.get<any[]>(`${this.baseUrl}/my`, this.headers());
  }

  createInvoice(data: any) {
    return this.http.post(`${this.baseUrl}/create`, data, this.headers());
  }

  payInvoice(id: number) {
    return this.http.post(`${this.baseUrl}/${id}/pay`, {}, this.headers());
  }

  cancelInvoice(id: number) {
    return this.http.post(`${this.baseUrl}/${id}/cancel`, {}, this.headers());
  }
}
