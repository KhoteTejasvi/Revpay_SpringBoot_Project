import { Routes } from '@angular/router';
import { LoginComponent } from './features/auth/login/login.component';
import { RegisterComponent } from './features/auth/register/register.component';
import { BusinessDashboardComponent } from './features/dashboard/dashboard.component';

export const routes: Routes = [

  { path: '', redirectTo: 'login', pathMatch: 'full' },

  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },

  { path: 'dashboard', component: BusinessDashboardComponent },
  { path: 'business-dashboard', component: BusinessDashboardComponent },
  {
    path: 'send-money',
    loadComponent: () =>
      import('./features/send-money/send-money.component')
        .then(m => m.SendMoneyComponent)
  },
{
  path: 'set-pin',
  loadComponent: () =>
    import('./features/set-pin/set-pin.component')
      .then(m => m.SetPinComponent)
},
{
  path: 'forgot-pin',
  loadComponent: () =>
    import('./features/forgot-pin/forgot-pin.component')
      .then(m => m.ForgotPinComponent)
},
  { path: '**', redirectTo: 'login' }

];
