
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

import { NavbarComponent } from '../../components/navbar/navbar';

import { CartService } from '../../services/cart.service';
import { WalletService } from '../../services/wallet';
import { OrderService } from '../../services/order';

declare var Razorpay: any;

@Component({
  selector: 'app-checkout',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    NavbarComponent
  ],
  templateUrl: './checkout.html',
  styleUrls: ['./checkout.css']
})
export class CheckoutComponent implements OnInit {

  // ===============================
  // Address
  // ===============================

  fullName = '';
  phone = '';
  house = '';
  street = '';
  city = '';
  state = '';
  pincode = '';

  // ===============================
  // Payment
  // ===============================

  paymentMethod = 'COD';

  // ===============================
  // Cart
  // ===============================

  cart: any[] = [];

  cartItems = 0;

  orderTotal = 0;

  // ===============================
  // Wallet
  // ===============================

  walletBalance = 0;

  // ===============================
  // UI
  // ===============================

  isLoading = false;

  successMsg = '';

  errorMsg = '';


  constructor(

    private cartService: CartService,

    private walletService: WalletService,

    private orderService: OrderService,

    private router: Router

  ) {}

  // ===================================
  // INIT
  // ===================================

  ngOnInit(): void {

    this.loadCart();

    this.loadWallet();

  }

  // ===================================
  // LOAD CART
  // ===================================

  loadCart(): void {

    this.cartService.getBackendCart().subscribe({

      next: (response: any) => {

        this.cart = response;

        this.calculateTotal();

      },

      error: (err) => {

        console.error(err);

        this.errorMsg = 'Unable to load cart.';

      }

    });

  }

  // ===================================
  // LOAD WALLET
  // ===================================

  loadWallet(): void {

    this.walletService.getWallet().subscribe({

      next: (response: any) => {

        this.walletBalance = response.balance;

      },

      error: (err) => {

        console.log(err);

      }

    });

  }

  // ===================================
  // CALCULATE TOTAL
  // ===================================

  calculateTotal(): void {

    this.cartItems = 0;

    this.orderTotal = 0;

    for (const item of this.cart) {

      this.cartItems += item.quantity;

      this.orderTotal += item.price * item.quantity;

    }

  }

  // ===================================
  // VALIDATION
  // ===================================

  validateForm(): boolean {

    this.successMsg = '';

    this.errorMsg = '';

    if (!this.fullName.trim()) {

      this.errorMsg = 'Please enter Full Name';

      return false;

    }

    if (!/^[6-9]\d{9}$/.test(this.phone)) {

      this.errorMsg = 'Enter valid Mobile Number';

      return false;

    }

    if (!this.house.trim()) {

      this.errorMsg = 'House / Flat No. is required';

      return false;

    }

    if (!this.street.trim()) {

      this.errorMsg = 'Street is required';

      return false;

    }

    if (!this.city.trim()) {

      this.errorMsg = 'City is required';

      return false;

    }

    if (!this.state.trim()) {

      this.errorMsg = 'State is required';

      return false;

    }

    if (!/^\d{6}$/.test(this.pincode)) {

      this.errorMsg = 'Enter valid Pincode';

      return false;

    }

    if (this.cart.length === 0) {

      this.errorMsg = 'Your cart is empty';

      return false;

    }

    return true;

  }

  // ===================================
  // ORDER REQUEST
  // ===================================

  private buildOrderRequest() {

    return {

      fullName: this.fullName,

      phone: this.phone,

      house: this.house,

      street: this.street,

      city: this.city,

      state: this.state,

      pincode: this.pincode,

      paymentMethod: this.paymentMethod

    };

  }

  // ===================================
  // PLACE ORDER
  // (Part 2)
  // ===================================

  // ===================================
// PLACE ORDER
// ===================================

placeOrder(): void {

  if (this.isLoading) {
    return;
  }

  if (!this.validateForm()) {
    return;
  }

  const request: any = {

    amount: this.orderTotal,

    razorpayOrderId: '',

    razorpayPaymentId: '',

    razorpaySignature: '',

    fullName: this.fullName,

    phone: this.phone,

    house: this.house,

    street: this.street,

    city: this.city,

    state: this.state,

    pincode: this.pincode,

    paymentMethod: this.paymentMethod

  };

  this.isLoading = true;

  if (this.paymentMethod === 'COD') {

    this.placeCodOrder(request);

  }

  else if (this.paymentMethod === 'WALLET') {

    this.placeWalletOrder(request);

  }

  else {

    this.startRazorpay(request);

  }

}

private placeCodOrder(request: any): void {

  this.orderService.checkout(request).subscribe({

    next: () => {

      this.isLoading = false;

      this.successMsg = 'Order placed successfully.';

      this.router.navigate(['/orders']);

    },

    error: (err) => {

      this.isLoading = false;

      this.errorMsg =
        err?.error?.message || 'Unable to place order.';

    }

  });

}

private placeWalletOrder(request: any): void {

  this.orderService.checkout(request).subscribe({

    next: () => {

      this.isLoading = false;

      this.successMsg = 'Order placed successfully.';

      this.router.navigate(['/orders']);

    },

    error: (err) => {

      this.isLoading = false;

      this.errorMsg =
        err?.error?.message || 'Wallet payment failed.';

    }

  });

}

private startRazorpay(request: any): void {

  this.orderService.createRazorpayOrder(this.orderTotal).subscribe({

    next: (order: any) => {

      const options = {

        key: order.key,

        amount: order.amount,

        currency: order.currency,

        order_id: order.orderId,

        name: 'E-Shopping Zone',

        description: 'Order Payment',

        prefill: {

          name: this.fullName,

          contact: this.phone

        },

        theme: {

          color: '#2563eb'

        },

        handler: (response: any) => {

          request.razorpayOrderId = response.razorpay_order_id;

          request.razorpayPaymentId = response.razorpay_payment_id;

          request.razorpaySignature = response.razorpay_signature;

          this.verifyPayment(request);

        }

      };

      const razorpay = new Razorpay(options);

      razorpay.open();

    },

    error: (err) => {

      this.isLoading = false;

      this.errorMsg =
        err?.error?.message || 'Unable to create Razorpay order.';

    }

  });

}

private verifyPayment(request: any): void {

  this.orderService.verifyPayment(request).subscribe({

    next: () => {

      this.isLoading = false;

      this.successMsg = 'Order placed successfully.';

      this.router.navigate(['/orders']);

    },

    error: (err) => {

      this.isLoading = false;

      this.errorMsg =
        err?.error?.message || 'Payment verification failed.';

    }

  });

}

}

