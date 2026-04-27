import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api';
import { NavbarComponent } from '../../components/navbar/navbar';
import { ProductCardComponent } from '../../components/product-card/product-card';
import { SearchService } from '../../services/search.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, FormsModule, NavbarComponent, ProductCardComponent],
  templateUrl: './home.html',
  styleUrls: ['./home.css']
})
export class HomeComponent implements OnInit {

  products: any[] = [];
  selectedCategory = '';

  searchTerm = '';
  minPrice: number | null = null;
  maxPrice: number | null = null;
  sortOrder = '';

  constructor(
    private api: ApiService,
    private cdr: ChangeDetectorRef,
    private searchService: SearchService 
  ) {}

  ngOnInit(): void {
    this.searchService.search$.subscribe(value => {
      this.searchTerm = value;
      this.loadProducts();
    });

    // 🔥 INITIAL LOAD
    this.loadProducts();
  }

  // 🔥 CATEGORY FILTER FUNCTION
  filterByCategory(category: string) {
    this.selectedCategory = category;
    this.loadProducts();
  }

  // 🔥 COMMON API CALL
  loadProducts() {
  let params: any = {};

  // ✅ CATEGORY (highest priority)
  if (this.selectedCategory) {
    params.category = this.selectedCategory;
  }

  // ✅ SEARCH
  if (this.searchTerm) {
    params.name = this.searchTerm;
  }

  // ✅ PRICE
  if (this.minPrice != null) {
    params.minPrice = this.minPrice;
  }

  if (this.maxPrice != null) {
    params.maxPrice = this.maxPrice;
  }

  // ✅ SORT
  if (this.sortOrder) {
    params.sort = this.sortOrder;
  }

  this.api.getProducts(params).subscribe({
    next: (res: any) => {
      console.log("API RESPONSE:", res);

      this.products = (res?.data || []).map((p: any) => ({
        ...p,
        imageUrl: this.getLocalImage(p.name),
        rating: (Math.random() * 2 + 3).toFixed(1)
      }));

      this.cdr.detectChanges();
    },

    error: (err) => {
      console.error("API ERROR:", err);
      this.products = [];
      this.cdr.detectChanges();
    }
  });
}

  // 🔥 IMAGE MAPPING
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