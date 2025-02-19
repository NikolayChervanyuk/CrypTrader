import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {CookieService} from 'ngx-cookie-service';
import {Observable} from 'rxjs';
import {API_URL} from '../../app.config';

@Injectable({
  providedIn: 'root'
})
export class AssetsService {

  constructor(
    private http: HttpClient,
    private cookieService: CookieService
  ) {
  }

  getTop20Assets(): Observable<any> {
    return this.http.get(`${API_URL}/assets/top-20`, {});
  }

  getAsset(symbol: string): Observable<any> {
    return this.http.get(`${API_URL}/assets`, {params: {symbol}});
  }
}
