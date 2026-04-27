import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { CartService } from '../../services/cart.service'; 

@Component({
  selector: 'app-product-card',
  standalone: true,
  imports: [CommonModule], // 🔥 IMPORTANT
  templateUrl: './product-card.html',
  styleUrls: ['./product-card.css']
})
export class ProductCardComponent {

  @Input() product: any;
  @Input() isCartPage: boolean = false;

  constructor(
    private router: Router,
    private cartService: CartService
  ) {}

  viewDetails() {
    this.router.navigate(['/product', this.product.id]);
  }

 addToCart(event: Event) {
  event.stopPropagation();

  const token = sessionStorage.getItem('token');

  // GUEST FLOW
  if (!token) {
    this.cartService.addToGuest(this.product);
    return;
  }

  const payload = {
    productId: this.product.id,
    quantity: 1
  };

  console.log("SENDING:", payload);

  this.cartService.addToBackend(payload).subscribe({
    next: (res) => {
      console.log("SUCCESS:", res);
      alert("Added to cart ✅");

      // 🔥 FIXED
      this.cartService.refreshCartCount();
    },
    error: (err) => {
      console.log("ERROR:", err);
      alert("Add to cart failed ❌");
    }
  });
}


getFinalPrice(product: any) {
  if (!product.discount) return product.price;

  return Math.round(
    product.price - (product.price * product.discount / 100)
  );
}
}