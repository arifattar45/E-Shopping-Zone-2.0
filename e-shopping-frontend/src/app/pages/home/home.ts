import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api';
import { NavbarComponent } from '../../components/navbar/navbar';
import { ProductCardComponent } from '../../components/product-card/product-card';
import { SearchService } from '../../services/search.service';
import { FooterComponent } from '../../components/footer/footer.component';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, FormsModule, NavbarComponent, ProductCardComponent, FooterComponent],
  templateUrl: './home.html',
  styleUrls: ['./home.css']
})
export class HomeComponent implements OnInit {

   // Pagination
currentPage = 0;
pageSize = 10;
totalPages = 0;
totalElements = 0;

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

  params.page = this.currentPage;
params.size = this.pageSize;

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

  console.log("Current Page:", this.currentPage);
console.log("Params:", params);

  this.api.getProducts(params).subscribe({
    next: (res: any) => {
      console.log("API RESPONSE:", res);

     this.products = (res.data || []).map((p: any) => ({
  ...p,
  rating: (Math.random() * 2 + 3).toFixed(1)
}));

this.totalPages = res.totalPages;
this.totalElements = res.totalElements;

      this.cdr.detectChanges();
    },

    error: (err) => {
      console.error("API ERROR:", err);
      this.products = [];
      this.cdr.detectChanges();
    }
  });
}

nextPage() {
  if (this.currentPage < this.totalPages - 1) {
    this.currentPage++;
    this.loadProducts();
  }
}

previousPage() {
  if (this.currentPage > 0) {
    this.currentPage--;
    this.loadProducts();
  }
}

goToPage(page: number) {
  this.currentPage = page;
  this.loadProducts();
}

}