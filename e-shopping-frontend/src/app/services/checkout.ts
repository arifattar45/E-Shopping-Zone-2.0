import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CheckoutService {

  private cartApi = 'http://localhost:8080/cart-service/cart';
  private orderApi = 'http://localhost:8080/order-service/orders';

  constructor(private http: HttpClient) {}

  getCart(): Observable<any[]> {
    return this.http.get<any[]>(this.cartApi);
  }

  createRazorpayOrder(amount: number): Observable<any> {
    return this.http.post(
      `${this.orderApi}/create-order`,
      { amount }
    );
  }

  verifyPayment(payload: any): Observable<any> {
    return this.http.post(
      `${this.orderApi}/verify-payment`,
      payload
    );
  }
}