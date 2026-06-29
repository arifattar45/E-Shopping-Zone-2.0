import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';

export const adminGuard: CanActivateFn = () => {

  const router = inject(Router);

  const token = sessionStorage.getItem('token');

  const role = sessionStorage.getItem('role');

  // ❌ NOT LOGGED IN
  if (!token) {

    router.navigate(['/login']);

    return false;
  }

  // ❌ NOT ADMIN
  if (role !== 'ROLE_ADMIN') {

    router.navigate(['/']);

    return false;
  }

  // ✅ ADMIN ALLOWED
  return true;
};