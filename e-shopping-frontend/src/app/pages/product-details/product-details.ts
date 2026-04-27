import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ApiService } from '../../services/api';
import { CommonModule } from '@angular/common';
import { CartService } from '../../services/cart.service';

@Component({
  standalone: true,
  imports: [CommonModule],
  templateUrl: './product-details.html',
  styleUrls: ['./product-details.css']
})
export class ProductDetailsComponent implements OnInit {

  product: any;
  quantity = 1;

  constructor(
    private route: ActivatedRoute,
    private api: ApiService,
    private cartService: CartService
  ) { }

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');

    this.api.getProductById(id).subscribe((res: any) => {
      this.product = res;
    });
  }

  addToCart() {
    const token = sessionStorage.getItem('token');

    console.log("FULL PRODUCT:", this.product);
console.log("ID:", this.product?.id);

    if (!token) {
      this.cartService.addToGuest({
        ...this.product,
        quantity: this.quantity
      });
    } else {
      this.cartService.addToBackend({
        ...this.product,
        quantity: this.quantity
      }).subscribe();
    }
  }
}