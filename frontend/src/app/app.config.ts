import {ApplicationConfig, provideZoneChangeDetection} from '@angular/core';
import {provideRouter} from '@angular/router';
import {CookieService} from 'ngx-cookie-service';
import {routes} from './app.routes';
import {provideHttpClient} from '@angular/common/http';
import {AuthService} from './_core/services/auth.service';
import {WebSocketService} from './_core/services/web-socket.service';
import {LoginComponent} from './auth/login/login.component';
import {AuthInterceptor} from './_core/utils/AuthInterceptor';
import {UserService} from './_core/services/user.service';
import {AssetsService} from './_core/services/assets.service';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({eventCoalescing: true}),
    provideRouter(routes),
    provideHttpClient(),
    AuthInterceptor,
    LoginComponent,
    CookieService,
    AuthService,
    UserService,
    AssetsService,
    WebSocketService
  ]
};

export const API_URL = 'http://localhost:8080';
export const API_AUTH_URL = 'http://localhost:8080/auth';
export const API_WEBSOCKET_URL = 'ws://localhost:8080/ticker-ws';
export const TITLE = 'CrypTrader - Your Trading Platform';
