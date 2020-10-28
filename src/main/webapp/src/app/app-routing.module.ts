import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {MainComponent} from "./main/main.component";
import {UserRouteAccessService} from "./auth/user-route-access-service";
import {LoginComponent} from "./login/login.component";
import {SignUpComponent} from "./sign-up/sign-up.component";


const routes: Routes = [

  {
    path: '',
    component: MainComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'login',
    component: LoginComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'sign-up',
    component: SignUpComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: '**',
    redirectTo: ''
  }

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
