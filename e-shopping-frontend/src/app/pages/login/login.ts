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

  constructor(private api: ApiService, private router: Router) { }

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

        // ✅ STORE TOKEN
        sessionStorage.setItem('token', res.token);

        // ✅ EXTRACT ROLE FROM JWT
        const payload = JSON.parse(atob(res.token.split('.')[1]));

        const role = payload.role;

        sessionStorage.setItem('role', role);
        sessionStorage.setItem('userEmail', this.email);

        // 🔥 MERGE GUEST CART
        const guestCart = JSON.parse(
          sessionStorage.getItem('guestCart') || '[]'
        );

        if (guestCart.length > 0) {

          guestCart.forEach((item: any) => {

            this.api.addToCart({
              productId: item.id,
              quantity: item.quantity || 1
            }).subscribe();

          });

          sessionStorage.removeItem('guestCart');
        }

        // ✅ ADMIN REDIRECT
        let url = '/';

        if (role === 'ROLE_ADMIN') {
          url = '/admin/dashboard';
        } else {
          url = sessionStorage.getItem('redirect') || '/cart';
        }

        sessionStorage.removeItem('redirect');

        setTimeout(() => {
          this.router.navigate([url]);
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

        this.successMsg = 'Register successful! Logging you in...';

        // 🔥 AUTO LOGIN (important)
        this.api.login({
          email: this.email,
          password: this.password
        }).subscribe((loginRes: any) => {

          // ✅ Save JWT token
          sessionStorage.setItem('token', loginRes.token);

          // Decode JWT
          const payload = JSON.parse(atob(loginRes.token.split('.')[1]));

          // Save user details
          sessionStorage.setItem('role', payload.role);
          sessionStorage.setItem('userEmail', this.email);

          // Merge guest cart
          const guestCart = JSON.parse(sessionStorage.getItem('guestCart') || '[]');

          if (guestCart.length > 0) {

            guestCart.forEach((item: any) => {
              this.api.addToCart({
                productId: item.id,
                quantity: item.quantity || 1
              }).subscribe();
            });

            sessionStorage.removeItem('guestCart');
          }

          const url = sessionStorage.getItem('redirect') || '/cart';
          sessionStorage.removeItem('redirect');

          this.router.navigate([url]);

        });

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