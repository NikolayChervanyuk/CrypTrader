import { Injectable } from '@angular/core';
import {Observable, Subject} from 'rxjs';
import {API_WEBSOCKET_URL} from '../../app.config';

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {

  private ws: WebSocket;
  private subject: Subject<any>;

  constructor() {
    this.ws = new WebSocket(API_WEBSOCKET_URL);
    this.subject = new Subject();

    this.ws.onmessage = (event) => {
      const data = JSON.parse(event.data);
      this.subject.next(data);
    };
  }

  listen(): Observable<any> {
    return this.subject.asObservable();
  }
}
