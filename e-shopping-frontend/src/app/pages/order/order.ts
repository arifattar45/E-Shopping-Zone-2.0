import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { NavbarComponent } from '../../components/navbar/navbar';

@Component({
  selector: 'app-order',
  standalone: true,
  imports: [CommonModule, NavbarComponent],
  templateUrl: './order.html',
  styleUrls: ['./order.css']
})
export class OrderComponent {

  orders: any[] = [];
  isLoading = true;

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.loadOrders();
  }

  loadOrders() {
  const token = sessionStorage.getItem('token');
  const role = sessionStorage.getItem('role'); // 🔥 get role

  if (!token) {
    this.orders = [];
    this.isLoading = false;
    return;
  }

  // 🔥 decide API based on role
  const url = role === 'ADMIN'
    ? 'http://localhost:8080/order-service/orders/all'
    : 'http://localhost:8080/order-service/orders';

  this.http.get(url, {
    headers: {
      Authorization: `Bearer ${token}`
    }
  }).subscribe({
    next: (res: any) => {
      this.orders = res || [];
      this.isLoading = false;
    },
    error: (err) => {
      console.log("Order fetch error:", err);
      this.orders = [];
      this.isLoading = false;
    }
  });
}


  cancelOrder(id: number) {
  const token = sessionStorage.getItem('token');

  if (!token) {
    alert("Please login");
    return;
  }

  if (!confirm("Are you sure you want to cancel this order?")) {
    return;
  }

  this.http.put(
    `http://localhost:8080/order-service/orders/${id}/cancel`,
    {}, // no body needed
    {
      headers: {
        Authorization: `Bearer ${token}`
      }
    }
  ).subscribe({
    next: () => {
      alert("✅ Order cancelled successfully");

      // 🔥 instant UI update (no delay issue)
      this.orders = this.orders.map(o => {
        if (o.id === id) {
          return { ...o, status: 'CANCELLED' };
        }
        return o;
      });

    },
    error: (err) => {
      console.log("CANCEL ERROR:", err);
      alert(err?.error?.message || "Cancel failed");
    }
  });
}
  
getTrackingStatus(status: string) {
  switch (status) {
    case 'PLACED': return 1;
    case 'SHIPPED': return 2;
    case 'OUT_FOR_DELIVERY': return 3;
    case 'DELIVERED': return 4;
    default: return 0;
  }
}

  getLocalImage(name: string): string {
  switch (name) {
    case 'iPhone 15': return 'assets/images/Apple-iPhone.jpg';
    case 'Dell Inspiron Laptop': return 'assets/images/dell-laptop.jpg';
    case 'Noise Smart Watch': return 'assets/images/noise-watch.jpg';
    case 'Sony Refrigerator': return 'assets/images/sony-refr.jpg';
    case 'OnePlus Phone': return 'assets/images/oneplus.jpg';
    case 'Samsung Galaxy S23': return 'assets/images/samsung.jpg';
    case 'HP Pavilion Laptop': return 'assets/images/hp-laptop.jpg';
    case 'Boat Rockerz 450': return 'assets/images/boat.jpg';
    case 'LG Washing Machine': return 'assets/images/lg-wash.jpg';
    case 'Puma Running Shoes': return 'assets/images/puma.jpg';
    default: return 'assets/images/Apple-iPhone.jpg';
  }
}
}