import { Component, OnInit } from '@angular/core'; // 🔥 add OnInit
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ApiService } from '../../services/api';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './login.html',
  styleUrls: ['./login.css']
})
export class LoginComponent implements OnInit {

  email = '';
  password = '';
  name = '';

  isRegisterMode = false;

  errorMsg = '';
  successMsg = '';

  constructor(private api: ApiService, private router: Router) {}

  // 🔥 BLOCK LOGIN IF ALREADY LOGGED IN
  ngOnInit() {
    const token = sessionStorage.getItem('token');
    if (token) {
      this.router.navigate(['/']); // already logged in → go home
    }
  }

  submit() {
    this.errorMsg = '';
    this.successMsg = '';

    if (!this.email.includes('@')) {
      this.errorMsg = 'Enter valid email';
      return;
    }

    if (this.password.length < 6) {
      this.errorMsg = 'Password must be at least 6 characters';
      return;
    }

    if (this.isRegisterMode && !this.name) {
      this.errorMsg = 'Name is required';
      return;
    }

    this.isRegisterMode ? this.register() : this.login();
  }

  login() {
  this.api.login({
    email: this.email,
    password: this.password
  }).subscribe({
    next: (res: any) => {

      this.successMsg = res?.message || 'Login successful';

      // ✅ STORE TOKEN + USER
      sessionStorage.setItem('token', res.token);
      sessionStorage.setItem('userEmail', this.email);

      // 🔥 STEP 1: MERGE GUEST CART → BACKEND
      const guestCart = JSON.parse(sessionStorage.getItem('guestCart') || '[]');

      if (guestCart.length > 0) {
        guestCart.forEach((item: any) => {
          this.api.addToCart({
            productId: item.id,
            quantity: item.quantity || 1
          }).subscribe();
        });

        sessionStorage.removeItem('guestCart'); // clear guest cart
      }

      // 🔥 STEP 2: REDIRECT
      const url = sessionStorage.getItem('redirect') || '/cart';
      sessionStorage.removeItem('redirect');

      setTimeout(() => {
        window.location.href = url; // keep your reload logic
      }, 700);

    },
    error: (err) => {
      this.errorMsg =
        err?.error?.message || 'Invalid credentials';
    }
  });
}

  register() {
    this.api.register({
      name: this.name,
      email: this.email,
      password: this.password
    }).subscribe({
      next: (res: any) => {

        this.successMsg = res?.message || 'Registered successfully';

        if (res.token) {
          sessionStorage.setItem('token', res.token);
          sessionStorage.setItem('userEmail', this.email);
        }

        setTimeout(() => {
          window.location.href = '/';
        }, 700);

      },
      error: (err) => {
        this.errorMsg =
          err?.error?.message || 'Registration failed';
      }
    });
  }

  toggleMode() {
    this.isRegisterMode = !this.isRegisterMode;
    this.errorMsg = '';
    this.successMsg = '';
  }
}