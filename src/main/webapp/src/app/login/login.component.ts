import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {User} from "../models/User";
import {AuthService} from "../auth/auth.service";
import {Principal} from "../auth/principal.service";
import {Login} from "../models/Login";
import {Router} from "@angular/router";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {


  loginForm: FormGroup;
  loggedInUser: User;
  loginError = {
    message: '',
    show: false
  }

  constructor(
    private fb: FormBuilder,
    private auth: AuthService,
    private principal: Principal,
    private router: Router
  ) {
  }

  ngOnInit(): void {
    this.watchPrincipal();
    this.loginForm = this.fb.group({
      username: ["", Validators.required],
      password: ["", Validators.required]
    })
  }

  watchPrincipal() {
    this.principal.identity(false).then((user) => {
      this.loggedInUser = user;
    });
    this.principal.getAuthenticationState().subscribe((user) => {
      this.loggedInUser = user;
    });
  }

  logOut() {
    this.auth.logout();
    this.principal.authenticate(null);
  }

  submit() {
    console.log("submitted", this.loginForm)
    if (!this.loginForm.valid) {
      return;
    }
    let val = this.loginForm.value;
    let user = new Login(val.username, val.password);

    this.auth.login(user).then((res) => {
      console.log(res);
      this.router.navigate([''])
      this.loginError.show = false;

    }).catch(err => {
      this.loginError.message = err.details
      this.loginError.show = true
    })
  }


}
