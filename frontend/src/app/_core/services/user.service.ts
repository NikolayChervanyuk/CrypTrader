import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {CookieService} from 'ngx-cookie-service';
import {Observable} from 'rxjs';
import {API_URL} from '../../app.config';
import {BuyRequest} from '../interfaces/reqresp/user/BuyRequest';
import {SellRequest} from '../interfaces/reqresp/user/SellRequest';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(
    private http: HttpClient,
    private cookieService: CookieService
  ) {
  }

  getUserDetails(): Observable<any> {
    return this.http.get(`${API_URL}/user`,
      {headers: {Authorization: `Bearer ${this.getAccessToken()}`}});
  }

  getAssets(): Observable<any> {
    return this.http.get(`${API_URL}/user/assets`,
      {headers: {Authorization: `Bearer ${this.getAccessToken()}`}});
  }

  getAsset(symbol: string): Observable<any> {
    return this.http.get(`${API_URL}/user/assets`,
      {params: {symbol}, headers: {Authorization: `Bearer ${this.getAccessToken()}`}});
  }

  getTradeHistory(): Observable<any> {
    return this.http.get(`${API_URL}/user/trade-history`,
      {headers: {Authorization: `Bearer ${this.getAccessToken()}`}});
  }

  buyCurrency(buyRequest: BuyRequest): Observable<any> {
    console.log(`Buy request model is ${buyRequest}`);
    return this.http.post(`${API_URL}/user/buy`, buyRequest,
      {headers: {Authorization: `Bearer ${this.getAccessToken()}`}}
    );
  }

  sellCurrency(sellRequest: SellRequest): Observable<any> {
    return this.http.post(`${API_URL}/user/sell`, sellRequest,
      {headers: {Authorization: `Bearer ${this.getAccessToken()}`}});
  }

  resetAccount(): Observable<any> {
    return this.http.post(`${API_URL}/user/reset`, {},
      {headers: {Authorization: `Bearer ${this.getAccessToken()}`}});
  }

  private getAccessToken(): string {
    return this.cookieService.get('access_token');
  }
}
