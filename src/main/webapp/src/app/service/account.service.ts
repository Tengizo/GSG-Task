import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {User} from "../models/User";
import {Observable} from "rxjs";
import {SERVER_API_URL} from "../app.constants";

let BASE_ACC_URL = SERVER_API_URL + "/account";

@Injectable({
  providedIn: 'root'
})
export class AccountService {

  constructor(private http: HttpClient) {
  }

  save(account: User): Observable<any> {
    return this.http.post(BASE_ACC_URL + '/sign-up', account);
  }

  update(account: User): Observable<any> {
    return this.http.post(BASE_ACC_URL + '/update', account);
  }

  getCurrent(): Observable<User> {
    return this.http.get<User>(BASE_ACC_URL + '');
  }
}
