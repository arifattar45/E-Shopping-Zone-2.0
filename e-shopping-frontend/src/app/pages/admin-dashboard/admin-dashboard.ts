import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';

import { AdminSidebarComponent } from '../../components/admin-sidebar/admin-sidebar.component';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,

  imports: [
    CommonModule,
    AdminSidebarComponent
  ],

  templateUrl: './admin-dashboard.html',
  styleUrls: ['./admin-dashboard.css']
})
export class AdminDashboard implements OnInit {

  totalRevenue = 0;
  totalOrders = 0;
  totalProducts = 0;
  totalUsers = 0;

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.loadDashboard();
  }

  loadDashboard() {

    const token = sessionStorage.getItem('token');

    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`
    });

    this.http.get<any>(
      'http://localhost:8080/order-service/orders/dashboard',
      { headers }
    )
    .subscribe({

      next: (res) => {

        console.log("DASHBOARD:", res);

        this.totalRevenue = res.totalRevenue;
        this.totalOrders = res.totalOrders;
        this.totalProducts = res.totalProducts;
        this.totalUsers = res.totalUsers;
      },

      error: (err) => {
        console.log(err);
      }

    });
  }
}