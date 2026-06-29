import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject } from 'rxjs'; // 🔥 already added

@Injectable({
  providedIn: 'root'
})
export class CartService {

  private baseUrl = 'http://localhost:8080/cart-service/cart';

  // 🔥 ADD HERE (inside class, top)
  cartCount$ = new BehaviorSubject<number>(0);

constructor(private http: HttpClient) {
  const cart = this.getGuestCart();
  this.updateCartCount(cart); // 🔥 correct initial count
}
  // =============================
  // 🟢 GUEST CART
  // =============================

  addToGuest(product: any) {
  let cart = JSON.parse(sessionStorage.getItem('guestCart') || '[]');

  const existing = cart.find((p: any) => p.id === product.id);

  if (existing) {
    existing.quantity = (existing.quantity || 1) + 1;
  } else {
    cart.push({ ...product, quantity: 1 });
  }

  sessionStorage.setItem('guestCart', JSON.stringify(cart));

  this.updateCartCount(cart); // 🔥 FIXED
}

updateCartCount(cart: any[]) {
  const total = cart.reduce(
    (sum, item) => sum + (item.quantity || 1),
    0
  );

  this.cartCount$.next(total);
}

  getGuestCart() {
    return JSON.parse(sessionStorage.getItem('guestCart') || '[]');
  }

  clearGuestCart() {
    sessionStorage.removeItem('guestCart');
    this.cartCount$.next(0); // 🔥 reset count
  }

  // =============================
  // 🔐 BACKEND CART
  // =============================

  addToBackend(data: any) {
  const token = sessionStorage.getItem('token');

  return this.http.post(
    'http://localhost:8080/cart-service/cart/add',
    data,
    {
      headers: {
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    }
  );
}

  removeFromGuest(index: number) {
  let cart = JSON.parse(sessionStorage.getItem('guestCart') || '[]');

  cart.splice(index, 1);

  sessionStorage.setItem('guestCart', JSON.stringify(cart));

  this.updateCartCount(cart);
}

removeFromBackend(id: number) {
  const token = sessionStorage.getItem('token');

  return this.http.delete(
    `${this.baseUrl}/remove/${id}`,
    {
      headers: {
        Authorization: `Bearer ${token}`
      },
      responseType: 'text' as 'json'   // ✅ FIX
    }
  );
}

  removeByProduct(product: any) {
  let cart = JSON.parse(sessionStorage.getItem('guestCart') || '[]');

  cart = cart.filter((p: any) => p.id !== product.id);

  sessionStorage.setItem('guestCart', JSON.stringify(cart));

  this.updateCartCount(cart);
}

increaseQty(index: number) {
  let cart = this.getGuestCart();

  cart[index].quantity = (cart[index].quantity || 1) + 1;

  sessionStorage.setItem('guestCart', JSON.stringify(cart));
  this.updateCartCount(cart);
}

decreaseQty(index: number) {
  let cart = this.getGuestCart();

  if (cart[index].quantity > 1) {
    cart[index].quantity -= 1;
  }

  sessionStorage.setItem('guestCart', JSON.stringify(cart));
  this.updateCartCount(cart);
}

getBackendCart() {
  const token = sessionStorage.getItem('token');

  return this.http.get(`${this.baseUrl}`, {
    headers: {
      Authorization: `Bearer ${token}`
    }
  });
}
refreshCartCount() {
  const token = sessionStorage.getItem('token');

  if (token) {
    this.getBackendCart().subscribe((res: any) => {

      const items = res.data || res;

      const total = items.reduce(
        (sum: number, item: any) => sum + item.quantity,
        0
      );

      this.cartCount$.next(total);
    });
  } else {
    const cart = this.getGuestCart();
    this.updateCartCount(cart);
  }
}

  decreaseBackend(productId: number) {
  const token = sessionStorage.getItem('token');

  return this.http.post(
    `${this.baseUrl}/decrease`,
    { productId, quantity: 1 },
    {
      headers: {
        Authorization: `Bearer ${token}`
      }
    }
  );
}
}