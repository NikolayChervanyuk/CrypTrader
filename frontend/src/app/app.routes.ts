import { Routes } from '@angular/router';
import {LoginComponent} from './auth/login/login.component';
import {AccountComponent} from './account/account.component';
import {HomeComponent} from './home/home.component';
import {AuthComponent} from './auth/auth.component';
import {RegisterComponent} from './auth/register/register.component';
import {TradeComponent} from './trade/trade.component';
import {NotFoundComponent} from './_core/components/not-found/not-found.component';
import {AuthGuard} from './_core/guards/auth.guard';
import {LayoutComponent} from './_core/components/layout/layout.component';

export const routes: Routes = [

  { path: 'auth', redirectTo: 'auth/login', pathMatch: 'full'},
  { path: 'auth/login', component: LoginComponent},
  { path: 'auth/register', component: RegisterComponent},
  {
    path: '',
    component: LayoutComponent,
    children: [
      { path: '', redirectTo: 'home', pathMatch: 'full' },
      { path: 'home', component: HomeComponent },
      { path: 'account/:username', component: AccountComponent, canActivate: [AuthGuard] },
      { path: 'trade/:symbol', component: TradeComponent, canActivate: [AuthGuard] },
      { path: '**', component: NotFoundComponent }
    ]
  }

  // { path: 'auth', component: AuthComponent, redirectTo: 'auth/login', pathMatch: 'full'},
  // { path: 'auth/login', component: LoginComponent},
  // { path: 'auth/register', component: RegisterComponent},
  // { path: '', redirectTo: 'home', pathMatch: 'full' },
  // { path: 'home', component: HomeComponent},
  // { path: 'account/:username', component: AccountComponent, canActivate: [AuthGuard]},
  // { path: 'trade/:symbol', component: TradeComponent, canActivate: [AuthGuard]},
  // { path: 'not-found', component: NotFoundComponent},
  // { path: '**', redirectTo: 'not-found', pathMatch: 'full' }
];
