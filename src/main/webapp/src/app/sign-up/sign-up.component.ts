import {Component, OnInit} from '@angular/core';
import {User} from "../models/User";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {AccountService} from "../service/account.service";
import {Router} from "@angular/router";
import {MatSnackBar} from "@angular/material/snack-bar";
import {getData} from "country-list";

@Component({
  selector: 'app-sign-up',
  templateUrl: './sign-up.component.html',
  styleUrls: ['./sign-up.component.scss']
})
export class SignUpComponent implements OnInit {

  signUpFor: FormGroup;
  submitting = false;
  user: User;
  countries: [];

  constructor(
    private fb: FormBuilder,
    private acccService: AccountService,
    private router: Router,
    private _snackBar: MatSnackBar
  ) {
    this.user = new User();
  }

  ngOnInit() {
    this.countries = getData()
    this.createNewForm();
  }

  createNewForm() {
    this.signUpFor = this.fb.group({
      username: [this.user.username, Validators.required],
      country: [this.user.country, Validators.required],
      jobInterval: [this.user.jobInterval, [Validators.required, Validators.min(1), Validators.max(60), Validators.pattern("\\+?\\d+")]],
      password: [this.user.password, [Validators.required]],
    })
  }

  openSnackBar(message: string, action: string) {
    this._snackBar.open(message, action, {
      duration: 2000,
    });
  }


  submit() {
    if (!this.signUpFor.valid) {
      console.log(this.signUpFor)
      return;
    }
    this.submitting = true;
    let val = this.signUpFor.value;
    this.user.username = val.username;
    this.user.country = val.country;
    this.user.jobInterval = val.jobInterval;
    this.user.password = val.password;

    this.acccService.save(this.user).subscribe((response) => {
      this.submitting = false;
      console.log(response);
      this.user = new User();
      this.createNewForm();
      this.router.navigate(['login'])
      this.openSnackBar("User was created successfully", "");
    }, error => {
      this.submitting = false;
      this.openSnackBar(`Error during user creation ${error}`, "");
    });
  }

}
