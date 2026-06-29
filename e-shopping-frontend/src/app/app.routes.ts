import { Routes } from '@angular/router';

import { HomeComponent } from './pages/home/home';
import { ProductDetailsComponent } from './pages/product-details/product-details';
import { CartComponent } from './pages/cart/cart';
import { LoginComponent } from './pages/login/login';
import { CheckoutComponent } from './pages/checkout/checkout';
import { OrderComponent } from './pages/order/order';

// 🔥 ADMIN
import { AdminLayoutComponent } from './pages/admin-layout/admin-layout';
import { AdminDashboard } from './pages/admin-dashboard/admin-dashboard';

import { AdminProducts } from './pages/admin-products/admin-products';
import { AdminOrdersComponent } from './pages/admin-orders/admin-orders';

import { AdminUsers } from './pages/admin-users/admin-users';
import { adminGuard } from './guards/admin-guard';

import { MyAccount  } from './pages/my-account/my-account';

export const routes: Routes = [

  // =========================
  // USER ROUTES
  // =========================

  {
    path: '',
    component: HomeComponent
  },

  {
    path: 'product/:id',
    component: ProductDetailsComponent
  },

  {
    path: 'cart',
    component: CartComponent
  },

  {
    path: 'login',
    component: LoginComponent
  },

  {
    path: 'checkout',
    component: CheckoutComponent
  },

  {
    path: 'order',
    component: OrderComponent
  },

  // =========================
  // ADMIN ROUTES
  // =========================

  {
    path: 'admin',
    component: AdminLayoutComponent,
    canActivate: [adminGuard],

    children: [

      {
        path: 'dashboard',
        component: AdminDashboard
      },

      {
        path: 'products',
        component: AdminProducts
      },

      {
        path: 'orders',
        component: AdminOrdersComponent
      },

      {
        path: 'users',
        component: AdminUsers
      },
      {
        path: 'admin/users',
        component: AdminUsers
      }

    ]
  },
  {
  path: 'my-account',
  component: MyAccount 
},

  // =========================
  // NOT FOUND
  // =========================

  {
    path: '**',
    redirectTo: ''
  }

];