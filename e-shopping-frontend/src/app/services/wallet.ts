import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class WalletService {

  private api = 'http://localhost:8080/wallet-service/wallet';

  constructor(private http: HttpClient) {}

  getWallet(): Observable<any> {
    return this.http.get(`${this.api}/me`);
  }

  getTransactions(): Observable<any[]> {
    return this.http.get<any[]>(`${this.api}/transactions`);
  }

  createOrder(amount: number) {
  return this.http.post<any>(
    `${this.api}/recharge/create-order`,
    { amount }
  );
}

verifyPayment(payment: any) {
  return this.http.post<any>(
    `${this.api}/recharge/verify`,
    payment
  );
}

}