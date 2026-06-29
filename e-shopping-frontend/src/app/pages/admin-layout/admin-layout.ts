import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

import { AdminSidebarComponent } from '../../components/admin-sidebar/admin-sidebar.component';
@Component({
  selector: 'app-admin-layout',
  standalone: true,
  imports: [
    RouterOutlet,
    AdminSidebarComponent
  ],
  templateUrl: './admin-layout.html',
styleUrl: './admin-layout.css'
})
export class AdminLayoutComponent {

}