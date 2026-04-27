import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  // 🔥 PRODUCT SERVICE
  baseUrl = 'http://localhost:8080/product-service/products';

  // 🔥 AUTH SERVICE (FIXED)
  authUrl = 'http://localhost:8080/user-service/auth';

  constructor(private http: HttpClient) {}

  // =============================
  // 🛒 PRODUCTS
  // =============================

  getAllProducts() {
    return this.http.get(`${this.baseUrl}?page=0&size=20`);
  }

  getProducts(params: any) {
    params.size = 50;
    return this.http.get(this.baseUrl, { params });
  }

  getProductById(id: any) {
    return this.http.get(`${this.baseUrl}/${id}`);
  }

  // =============================
  // 🔐 AUTH (FIXED)
  // =============================

  login(data: any) {
    return this.http.post(`${this.authUrl}/login`, data);
  }

  register(data: any) {
    return this.http.post(`${this.authUrl}/register`, data);
  }

  addToCart(data: any) {
  const token = sessionStorage.getItem('token');

  return this.http.post(
    'http://localhost:8080/cart-service/cart/add',
    data,
    {
      headers: {
        Authorization: `Bearer ${token}`
      }
    }
  );
}

getMyOrders() {
  const token = sessionStorage.getItem('token');

  return this.http.get(
    'http://localhost:8080/order-service/orders',
    {
      headers: {
        Authorization: `Bearer ${token}`
      }
    }
  );
}


}