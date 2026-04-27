import { Routes } from '@angular/router';
import { HomeComponent } from './pages/home/home';
import { ProductDetailsComponent } from './pages/product-details/product-details';
import { CartComponent } from './pages/cart/cart';
import { LoginComponent } from './pages/login/login';
import { CheckoutComponent } from './pages/checkout/checkout';
import { OrderComponent } from './pages/order/order';
import { AdminOrdersComponent } from './pages/admin-orders/admin-orders';

export const routes: Routes = [
  { path: '', component: HomeComponent },

  { path: 'product/:id', component: ProductDetailsComponent },

  { path: 'cart', component: CartComponent },
  { path: 'login', component: LoginComponent },

  { path: 'checkout', component: CheckoutComponent },
  { path: 'order', component: OrderComponent },

  { path: 'admin/orders', component: AdminOrdersComponent },

  { path: '**', redirectTo: '' }
];