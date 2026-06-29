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
    return this.http.get(`${this.baseUrl}?page=0&size=10`);
  }

  getProducts(params: any) {
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


getAdminProducts() {

  const token = sessionStorage.getItem('token');

  return this.http.get(
    'http://localhost:8080/product-service/products?page=0&size=50',
    {
      headers: {
        Authorization: `Bearer ${token}`
      }
    }
  );

}

addProduct(data: any) {

  const token = sessionStorage.getItem('token');

  return this.http.post(
    'http://localhost:8080/product-service/products',
    data,
    {
      headers: {
        Authorization: `Bearer ${token}`
      }
    }
  );
}

updateProduct(id: number, data: any) {

  const token = sessionStorage.getItem('token');

  return this.http.put(
    `http://localhost:8080/product-service/products/${id}`,
    data,
    {
      headers: {
        Authorization: `Bearer ${token}`
      }
    }
  );
}

deleteProduct(id: number) {

  const token = sessionStorage.getItem('token');

  return this.http.delete(
    `http://localhost:8080/product-service/products/${id}`,
    {
      headers: {
        Authorization: `Bearer ${token}`
      }
    }
  );
}


getAllOrders() {

  const token = sessionStorage.getItem('token');

  return this.http.get(
    'http://localhost:8080/order-service/orders/all',
    {
      headers: {
        Authorization: `Bearer ${token}`
      }
    }
  );

}

updateOrderStatus(id: number, status: string) {

  const token = sessionStorage.getItem('token');

  return this.http.put(
    `http://localhost:8080/order-service/orders/${id}/status?value=${status}`,
    {},
    {
      headers: {
        Authorization: `Bearer ${token}`
      }
    }
  );

}

}