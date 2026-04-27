import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { NavbarComponent } from '../../components/navbar/navbar';

@Component({
  selector: 'app-admin-orders',
  standalone: true,
  imports: [CommonModule, NavbarComponent],
  templateUrl: './admin-orders.html',
  styleUrls: ['./admin-orders.css']
})
export class AdminOrdersComponent {

  orders: any[] = [];

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.getAllOrders();
  }

  getAllOrders() {
    const token = sessionStorage.getItem('token');

    this.http.get(
      'http://localhost:8080/order-service/orders/all',
      {
        headers: {
          Authorization: `Bearer ${token}`
        }
      }
    ).subscribe((res: any) => {
      this.orders = res;
    });
  }

  updateStatus(id: number, status: string) {
    const token = sessionStorage.getItem('token');

    this.http.put(
      `http://localhost:8080/order-service/orders/${id}/status?value=${status}`,
      {},
      {
        headers: {
          Authorization: `Bearer ${token}`
        }
      }
    ).subscribe(() => {
      this.getAllOrders();
    });
  }
}