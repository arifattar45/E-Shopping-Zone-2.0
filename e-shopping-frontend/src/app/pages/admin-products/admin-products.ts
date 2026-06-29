import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ApiService } from '../../services/api';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-admin-products',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-products.html',
  styleUrl: './admin-products.css'
})
export class AdminProducts implements OnInit {

  products: any[] = [];

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.loadProducts();
  }

  showForm = false;

isEdit = false;

selectedId: number | null = null;

productData = {

  name: '',
  description: '',
  price: 0,
  category: '',
  brand: '',
  imageUrl: ''

};

  loadProducts() {

  console.log("Loading products...");

  this.api.getAdminProducts().subscribe({

    next: (res: any) => {

      console.log("API RESPONSE:", res);

      this.products = res.data;

    },

    error: (err) => {

      console.log("API ERROR:", err);

    }

  });

}

  deleteProduct(id: number) {

  if (!confirm('Delete product?')) return;

  this.api.deleteProduct(id).subscribe({

    next: () => {

      alert('Deleted successfully');

      this.loadProducts();

    },

    error: (err) => {

      console.log(err);

      alert('Delete failed');

    }

  });

}


  addProduct() {

  this.api.addProduct(this.productData).subscribe({

    next: () => {

      alert('Product added');

      this.showForm = false;

      this.resetForm();

      this.loadProducts();

      this.showForm = false;

    },

    error: (err) => {

      console.log(err);

      alert('Add failed');

    }

  });

}


editProduct(product: any) {

  this.showForm = true;

  this.isEdit = true;

  this.selectedId = product.id;

  this.productData = {

    name: product.name,
    description: product.description,
    price: product.price,
    category: product.category,
    brand: product.brand,
    imageUrl: product.imageUrl

  };

}


updateProduct() {

  this.api.updateProduct(
    this.selectedId!,
    this.productData
  ).subscribe({

    next: () => {

      alert('Product updated');

      this.showForm = false;

      this.resetForm();

      this.loadProducts();

      this.showForm = false;

    },

    error: (err) => {

      console.log(err);

      alert('Update failed');

    }

  });

}

resetForm() {

  this.productData = {

    name: '',
    description: '',
    price: 0,
    category: '',
    brand: '',
    imageUrl: ''

  };

  this.isEdit = false;

  this.selectedId = null;

}

}