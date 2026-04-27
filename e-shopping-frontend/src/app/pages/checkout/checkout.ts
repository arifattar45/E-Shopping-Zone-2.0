import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { NavbarComponent } from '../../components/navbar/navbar';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-order',
  standalone: true,
  imports: [CommonModule, NavbarComponent, FormsModule],
  templateUrl: './checkout.html',
  styleUrls: ['./checkout.css']
})
export class CheckoutComponent {

  address = '';
  paymentMethod = 'COD';

  successMsg = '';
  errorMsg = '';
  isLoading = false;

  constructor(private http: HttpClient, private router: Router) { }

  placeOrder() {
    const token = sessionStorage.getItem('token');

    if (!token) {
      this.router.navigate(['/login']);
      return;
    }

    if (!this.address.trim()) {
      this.errorMsg = "Address required";
      return;
    }

    const payload = {
      address: this.address,
      paymentMethod: this.paymentMethod
    };

    this.isLoading = true;
    this.errorMsg = '';
    this.successMsg = '';

    this.http.post(
      'http://localhost:8080/order-service/orders/checkout',
      payload,
      {
        headers: {
          Authorization: `Bearer ${token}`
        }
      }
    ).subscribe({
      next: (res: any) => {
        this.successMsg = "✅ Order placed successfully!";
        this.isLoading = false;

        // 🔥 update cart count (important)
        sessionStorage.removeItem('guestCart'); // optional safety

        // wait before redirect
        setTimeout(() => {
          this.router.navigate(['/order']); // go to orders page
        }, 1500);
      },
      error: (err) => {
        this.errorMsg = err?.error?.message || "Order failed";
        this.isLoading = false;
      }
    });
  }
}