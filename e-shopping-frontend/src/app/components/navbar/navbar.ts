import { Component, OnInit } from '@angular/core';
import { SearchService } from '../../services/search.service';
import { CartService } from '../../services/cart.service';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterModule, CommonModule], 
  templateUrl: './navbar.html',
  styleUrls: ['./navbar.css']
})
export class NavbarComponent implements OnInit {

  cartCount = 0;
  

  constructor(
    private searchService: SearchService,
    private cartService: CartService
  ) {}

  ngOnInit(): void {
  this.cartService.cartCount$.subscribe(count => {
    this.cartCount = count;
  });
}


  onSearch(value: string) {
    this.searchService.setSearch(value);
  }

  // 🔥 GET CART COUNT (guest for now)
  updateCartCount() {
    const guestCart = this.cartService.getGuestCart();
    this.cartCount = guestCart.length;
  }

  isLoggedIn = !!sessionStorage.getItem('token');
userEmail = sessionStorage.getItem('userEmail');

logout() {
  sessionStorage.clear();
  window.location.href = '/';
}
}