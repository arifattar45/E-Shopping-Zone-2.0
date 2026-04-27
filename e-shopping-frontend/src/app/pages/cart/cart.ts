import { ChangeDetectorRef } from '@angular/core';
import { RouterModule } from '@angular/router';
import { Router } from '@angular/router';
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CartService } from '../../services/cart.service';
import { NavbarComponent } from '../../components/navbar/navbar';


@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, NavbarComponent, RouterModule],
  templateUrl: './cart.html',
  styleUrls: ['./cart.css']
})
export class CartComponent {

  cartItems: any[] = [];
  isLoggedIn = false;

  constructor(
    private cartService: CartService,
    private router: Router,
    private cd: ChangeDetectorRef   // 🔥 add this
  ) { }

  ngOnInit() {
    this.isLoggedIn = !!sessionStorage.getItem('token');

    // 🔥 FORCE API CALL AFTER COMPONENT LOAD
    setTimeout(() => {
      this.loadCart();
      this.cartService.refreshCartCount();
    }, 0);
  }


  increase(item: any, index: number) {
    const token = sessionStorage.getItem('token');

    if (token) {
      item.quantity++;   // ✅ UI update

      this.cartService.addToBackend({
        productId: item.productId,
        quantity: 1
      }).subscribe(() => {
        this.cartService.refreshCartCount();
      });

    } else {
      this.cartService.increaseQty(index);
      this.cartItems = this.cartService.getGuestCart();
    }
  }

  decrease(item: any, index: number) {
    const token = sessionStorage.getItem('token');

    if (token) {
      if (item.quantity <= 1) return;

      item.quantity--;   // ✅ UI update ONLY

      // ❌ DON'T call backend decrease
      // backend is broken for decrease

      this.cartService.refreshCartCount();

    } else {
      this.cartService.decreaseQty(index);
      this.cartItems = this.cartService.getGuestCart();
    }
  }

  getTotal() {
    return this.cartItems.reduce(
      (sum, item) => sum + (item.price * item.quantity),
      0
    );
  }

  showLoginCard = false;

  checkout() {
    const token = sessionStorage.getItem('token');

    if (!token) {
      sessionStorage.setItem('redirect', '/order'); // 🔥 save target
      this.router.navigate(['/login']);
      return;
    }

    this.router.navigate(['/checkout']);
  }

  goToLogin() {
    this.router.navigate(['/login']);
  }

  isLoading = true;

  loadCart() {
    this.isLoading = true;

    const token = sessionStorage.getItem('token');

    if (token) {
      this.cartService.getBackendCart().subscribe({
        next: (res: any) => {
          const data = res.data || res || [];

          this.cartItems = [...data];   // 🔥 force new reference
          this.cd.detectChanges();      // 🔥 force UI refresh

          this.isLoading = false;
      },
        error: () => {
          this.cartItems = [];
          this.isLoading = false;
        }
    });
  } else {
  this.cartItems = [...this.cartService.getGuestCart()];
  this.isLoading = false;
}
}

removeItem(item: any, index: number) {
  const token = sessionStorage.getItem('token');

  if (token) {
    this.cartService.removeFromBackend(item.id).subscribe(() => {
      this.loadCart();
      this.cartService.refreshCartCount();
    });
  } else {
    this.cartService.removeFromGuest(index);
    this.cartItems = this.cartService.getGuestCart();
  }
}



  getLocalImage(name: string): string {
  switch (name) {
    case 'iPhone 15':
      return 'assets/images/Apple-iPhone.jpg';
    case 'Dell Inspiron Laptop':
      return 'assets/images/dell-laptop.jpg';
    case 'Noise Smart Watch':
      return 'assets/images/noise-watch.jpg';
    case 'Sony Refrigerator':
      return 'assets/images/sony-refr.jpg';
    case 'OnePlus Phone':
      return 'assets/images/oneplus.jpg';
    case 'Samsung Galaxy S23':
      return 'assets/images/samsung.jpg';
    case 'HP Pavilion Laptop':
      return 'assets/images/hp-laptop.jpg';
    case 'Boat Rockerz 450':
      return 'assets/images/boat.jpg';
    case 'LG Washing Machine':
      return 'assets/images/lg-wash.jpg';
    case 'Puma Running Shoes':
      return 'assets/images/puma.jpg';
    default:
      return 'assets/images/Apple-iPhone.jpg';
  }
}
}