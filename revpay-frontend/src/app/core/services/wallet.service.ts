import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface TransferRequest {
  receiverEmail: string;
  amount: number;
  note?: string;
  transactionPin: string;
}

@Injectable({
  providedIn: 'root'
})
export class WalletService {

  private baseUrl = 'http://localhost:8080/api/wallet';

  constructor(private http: HttpClient) {}

  transferMoney(data: TransferRequest): Observable<any> {
    return this.http.post(`${this.baseUrl}/transfer`, data);
  }

}
