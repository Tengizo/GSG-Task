import {Injectable} from '@angular/core';
import {BehaviorSubject, Observable} from "rxjs";
import {SERVER_SOCKET_URL} from "../app.constants";
import {Stomp} from '@stomp/stompjs';
import * as SockJS from 'sockjs-client';

let SOCKET_URL = SERVER_SOCKET_URL;

@Injectable({
  providedIn: 'root'
})
export class SocketService {
  private connectedSubject = new BehaviorSubject<boolean>(true);
  private notificationSubject = new BehaviorSubject<any>(null);

  public connected$ = this.connectedSubject.asObservable();
  public notifications$ = this.notificationSubject.asObservable();
  public connected = false;
  private stompClient = null;
  constructor() {
  }
  public connect(dest: any) {
    this.connected = true;
    this.stompClient = Stomp.over(function () {
      return new SockJS(SOCKET_URL)
    });

    const _this = this;
    this.stompClient.connect({}, function (frame) {
      _this.connectedSubject.next(true);
      _this.stompClient.subscribe(`/yt/${dest}`, function (message) {
        let notif = JSON.parse(message.body);
        console.log(notif);
        _this.notificationSubject.next(notif);
      });
    });
  }
  disconnect() {
    if (this.stompClient != null) {
      this.stompClient.disconnect();
    }

    this.connectedSubject.next(false);
    console.log('Disconnected!');
  }
}
