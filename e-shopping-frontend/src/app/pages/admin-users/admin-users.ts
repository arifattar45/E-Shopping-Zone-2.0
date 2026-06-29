import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpHeaders } from '@angular/common/http';

import { AdminSidebarComponent } from '../../components/admin-sidebar/admin-sidebar.component';

@Component({
  selector: 'app-admin-users',
  standalone: true,

  imports: [
    CommonModule,
    FormsModule,
    AdminSidebarComponent
  ],

  templateUrl: './admin-users.html',
  styleUrls: ['./admin-users.css']
})
export class AdminUsers {

  users: any[] = [];
  filteredUsers: any[] = [];

  searchText: string = '';

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.loadUsers();
  }

  loadUsers() {

    const token = sessionStorage.getItem('token');

    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`
    });

    this.http.get<any[]>(
      'http://localhost:8080/user-service/admin/users',
      { headers }
    ).subscribe(res => {

      this.users = res;
      this.filteredUsers = res;

    });
  }

  searchUsers() {

    this.filteredUsers = this.users.filter(user =>

      user.name?.toLowerCase().includes(this.searchText.toLowerCase()) ||

      user.email?.toLowerCase().includes(this.searchText.toLowerCase())

    );
  }

  deleteUser(id: number) {

    if (!confirm('Delete this user?')) return;

    const token = sessionStorage.getItem('token');

    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`
    });

    this.http.delete(
  `http://localhost:8080/user-service/admin/delete/${id}`,
  {
    headers,
    responseType: 'text'
  }
).subscribe(() => {

  this.loadUsers();

});
  }

}