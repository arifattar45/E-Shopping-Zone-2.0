declare var Razorpay: any;

import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { UserService } from '../../services/user';
import { WalletService } from '../../services/wallet';

import { User } from '../../model/user.model';
import { Wallet } from '../../model/wallet.model';
import { WalletTransaction } from '../../model/transaction.model';

import { NavbarComponent } from '../../components/navbar/navbar';
import { OrderService } from '../../services/order';

import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-my-account',
  standalone: true,
  imports: [CommonModule, FormsModule, NavbarComponent, RouterLink],
  templateUrl: './my-account.html',
  styleUrls: ['./my-account.css']
})
export class MyAccount implements OnInit {

  showAddMoneyModal = false;
  rechargeAmount = 0;
  showTransactionModal = false;

  user?: User;
  wallet?: Wallet;
  transactions: WalletTransaction[] = [];
  recentOrders: any[] = [];

  constructor(
    private userService: UserService,
    private walletService: WalletService,
    private orderService: OrderService
  ) {}

  ngOnInit(): void {

    console.log("MyAccount Loaded")

    this.loadProfile();
    this.loadWallet();
    this.loadTransactions();
    this.loadRecentOrders();
  }

  loadProfile(): void {

  console.log("Calling Profile API");

  this.userService.getProfile().subscribe({

    next: (res) => {
      console.log("PROFILE RESPONSE:", res);
      this.user = res;
    },

    error: (err) => {
      console.error("PROFILE ERROR:", err);
    }

  });

}

  loadWallet(): void {
    this.walletService.getWallet().subscribe({
      next: (res) => {
        this.wallet = res;
      },
      error: (err) => {
        console.error('Failed to load wallet', err);
      }
    });
  }

  loadTransactions(): void {
    this.walletService.getTransactions().subscribe({
      next: (res) => {
        this.transactions = res;
      },
      error: (err) => {
        console.error('Failed to load transactions', err);
      }
    });
  }

  // Open Modal
  addMoney(): void {
    this.showAddMoneyModal = true;
  }

  // Close Modal
  closeModal(): void {
    this.showAddMoneyModal = false;
    this.rechargeAmount = 0;
  }

  // Razorpay Payment
  continuePayment(): void {

    if (this.rechargeAmount <= 0) {
      alert('Please enter a valid amount');
      return;
    }

    this.walletService.createOrder(this.rechargeAmount).subscribe({
      

      next: (order) => {

        console.log("Order Response:", order);

        const options = {

          key: order.key,

          amount: order.amount,

          currency: order.currency,

          name: 'E-Shopping Zone',

          description: 'Wallet Recharge',

          order_id: order.orderId,

          prefill: {
            name: this.user?.name,
            email: this.user?.email
          },

          theme: {
            color: '#2563eb'
          },

          handler: (response: any) => {

            console.log("Payment Success:", response);

            const verifyRequest = {

              amount: this.rechargeAmount,

              razorpayOrderId: response.razorpay_order_id,

              razorpayPaymentId: response.razorpay_payment_id,

              razorpaySignature: response.razorpay_signature

            };

            this.walletService.verifyPayment(verifyRequest).subscribe({

              next: (res) => {

                 console.log("Verify Response:", res);

                alert(res.message);

                this.closeModal();

                this.loadWallet();

                this.loadTransactions();

              },

              error: (err) => {

                console.error(err);

                alert('Payment verification failed.');

              }

            });

          }

        };

          console.log("Opening Razorpay...");
        const razorpay = new Razorpay(options);

        razorpay.open();

      },

      error: (err) => {

        console.error(err);

        alert('Unable to create Razorpay order.');

      }

    });

  }


  loadRecentOrders(): void {

  this.orderService.getMyOrders().subscribe({

    next: (orders) => {

      this.recentOrders = orders
        .sort((a, b) =>
          new Date(b.createdAt).getTime() -
          new Date(a.createdAt).getTime()
        )
        .slice(0, 3);

    },

    error: (err) => {

      console.error("Failed to load orders", err);

    }

  });

}

showAllTransactions() {
  this.showTransactionModal = true;
}

closeTransactionModal() {
  this.showTransactionModal = false;
}
}