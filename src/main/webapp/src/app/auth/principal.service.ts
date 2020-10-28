import {Injectable} from '@angular/core';
import {Observable, Subject} from 'rxjs';
import {User} from "../models/User";
import {AccountService} from "../service/account.service";

@Injectable({providedIn: 'root'})
export class Principal {
  private userIdentity: User;
  private authenticated = false;
  private authenticationState = new Subject<User>();

  constructor(private account: AccountService) {
  }

  authenticate(identity) {
    this.userIdentity = identity;
    this.authenticated = identity !== null;
    this.authenticationState.next(this.userIdentity);
  }


  identity(force?: boolean): Promise<User> {
    if (force === true) {
      this.userIdentity = undefined;
    }

    // check and see if we have retrieved the userIdentity data from the server.
    // if we have, reuse it by immediately resolving
    if (this.userIdentity) {
      return Promise.resolve(this.userIdentity);
    }

    // retrieve the userIdentity data from the server, update the identity object, and then resolve.
    return this.account
      .getCurrent()
      .toPromise()
      .then(user => {

        if (user) {
          this.userIdentity = user;
          this.authenticated = true;
        } else {
          this.userIdentity = null;
          this.authenticated = false;
        }
        this.authenticationState.next(this.userIdentity);
        return this.userIdentity;
      })
      .catch(err => {
        this.userIdentity = null;
        this.authenticated = false;
        this.authenticationState.next(this.userIdentity);
        return null;
      });
  }

  isAuthenticated(): boolean {
    return this.authenticated;
  }

  isIdentityResolved(): boolean {
    return this.userIdentity !== undefined;
  }

  getAuthenticationState(): Observable<any> {
    return this.authenticationState.asObservable();
  }

}
