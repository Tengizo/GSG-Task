import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot} from '@angular/router';
import {Principal} from "./principal.service";


@Injectable({providedIn: 'root'})
export class UserRouteAccessService implements CanActivate {
  constructor(
    private router: Router,
    private principal: Principal
  ) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Promise<boolean> {
    return this.checkLogin(state.url);
  }

  checkLogin(url: string): Promise<boolean> {
    return this.principal.identity(true).then(account => {
      let isLogin = url.includes("login") || url.includes("sign-up");
      if (account) {
        if (isLogin) {
          this.router.navigate(['']);
          return false
        }
        return true;
      } else if (isLogin) {
        return true
      }
      this.router.navigate(['login']);
      return false;
    })
  }
}
