import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class OrderService {

  private api = 'http://localhost:8080/order-service/orders';

  constructor(private http: HttpClient) {}

  getMyOrders(): Observable<any[]> {
    return this.http.get<any[]>(this.api);
  }

  checkout(order: any): Observable<any> {
    return this.http.post(
      `${this.api}/checkout`,
      order
    );
  }

  createRazorpayOrder(amount: number): Observable<any> {
    return this.http.post(
      `${this.api}/create-order`,
      {
        amount: amount
      }
    );
  }

  verifyPayment(payment: any): Observable<any> {
    return this.http.post(
      `${this.api}/verify-payment`,
      payment
    );
  }
}