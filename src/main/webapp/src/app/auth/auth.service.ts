import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {SERVER_API_URL} from "../app.constants";
import {map} from "rxjs/operators";
import {Principal} from "./principal.service";
import {Router} from "@angular/router";

let BASE_ACC_URL = SERVER_API_URL + "/account";

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private http: HttpClient, private principal: Principal, private router: Router) {
  }

  static getToken() {
    return localStorage.getItem('token');
  }

  login(credentials): Promise<any> {
    return new Promise((resolve, reject) => {
      this.authenticate(credentials).subscribe(
        data => {
          this.principal.identity(true).then(account => {
            resolve(data);
          });
        },
        err => {
          this.logout();
          reject(err);
        }
      )
    });
  }

  authenticate(credentials): Observable<any> {
    return this.http.post(BASE_ACC_URL + '/login', credentials)
      .pipe(map(authenticateSuccess.bind(this)));

    function authenticateSuccess(resp) {
      const jwt = resp.token;
      AuthService.storeAuthenticationToken(jwt);
      return jwt;
    }
  }

  loginWithToken(jwt) {
    if (jwt) {
      AuthService.storeAuthenticationToken(jwt);
      return Promise.resolve(jwt);
    } else {
      return Promise.reject('auth-jwt-service Promise reject'); // Put appropriate error message here
    }
  }

  static storeAuthenticationToken(jwt) {
    localStorage.setItem('token', jwt);
  }

  logout() {
    localStorage.removeItem('token');
    this.router.navigate(['login']);
  }

}

