import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import { Router } from '@angular/router';
import { CookieService } from 'ngx-cookie-service';
import { Observable } from 'rxjs';
import {API_AUTH_URL} from '../../app.config';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(
    private http: HttpClient,
    private cookieService: CookieService
  ) { }

  login(credentials: { identifier: string; password: string }): Observable<any> {
    return this.http.post(`${API_AUTH_URL}/login`, credentials);
  }

  register(userData: { email: string; username: string; password: string }): Observable<any> {
    return this.http.post(`${API_AUTH_URL}/register`, userData);
  }

  setTokens(accessToken: string, refreshToken: string) {
    this.setAccessToken(accessToken);
    this.setRefreshToken(refreshToken);
  }

  setAccessToken(accessToken: string) {
    this.cookieService.set('access_token', accessToken, { path: '/', sameSite: 'Lax' });
  }
  setRefreshToken(refreshToken: string) {
    this.cookieService.set('refresh_token', refreshToken, { path: '/', sameSite: 'Lax' });
  }

  getToken(): string {
    return this.cookieService.get('access_token');
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  logout() {
    this.http.post(`${API_AUTH_URL}/logout`, {}).subscribe();
    this.cookieService.delete('access_token', '/');
    this.cookieService.delete('refresh_token', '/');
  }
}
