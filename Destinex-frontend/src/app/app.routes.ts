import { Routes } from '@angular/router';
import { adminGuard } from './core/guards/admin.guard';
import { authGuard } from './core/guards/auth.guard';
import { guestGuard } from './core/guards/guest.guard';
import { ShellComponent } from './layout/shell.component';

export const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    loadComponent: () => import('./pages/landing/landing.component').then((m) => m.LandingComponent),
  },
  {
    path: 'login',
    canActivate: [guestGuard],
    loadComponent: () => import('./pages/login/login.component').then((m) => m.LoginComponent),
  },
  {
    path: 'signup',
    canActivate: [guestGuard],
    loadComponent: () => import('./pages/signup/signup.component').then((m) => m.SignupComponent),
  },
  {
    path: '',
    component: ShellComponent,
    canActivate: [authGuard],
    children: [
      {
        path: 'destinations',
        loadComponent: () =>
          import('./pages/user/destination-list/destination-list.component').then(
            (m) => m.DestinationListComponent
          ),
      },
      {
        path: 'destinations/:id',
        loadComponent: () =>
          import('./pages/user/destination-detail/destination-detail.component').then(
            (m) => m.DestinationDetailComponent
          ),
      },
      {
        path: 'wishlist',
        loadComponent: () =>
          import('./pages/user/wishlist/wishlist.component').then((m) => m.WishlistComponent),
      },
      {
        path: 'admin/curate',
        canActivate: [adminGuard],
        loadComponent: () =>
          import('./pages/admin/import-countries/import-countries.component').then(
            (m) => m.ImportCountriesComponent
          ),
      },
      { path: '', pathMatch: 'full', redirectTo: 'destinations' },
    ],
  },
  { path: '**', redirectTo: '' },
];
