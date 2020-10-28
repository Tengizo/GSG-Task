import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {User} from "../models/User";
import {AccountService} from "../service/account.service";
import {Router} from "@angular/router";
import {MatSnackBar} from "@angular/material/snack-bar";
import {Principal} from "../auth/principal.service";
import {getData} from "country-list";
import {SocketService} from "../service/socket.service";
import {AuthService} from "../auth/auth.service";
import {YoutubeService} from "../service/youtube.service";

@Component({
  selector: 'app-main',
  templateUrl: './main.component.html',
  styleUrls: ['./main.component.scss']
})
export class MainComponent implements OnInit {
  updateForm: FormGroup;
  submitting = false;
  user: User;
  countries: [];
  apiLoaded = false;
  commentText = '';

  constructor(
    private fb: FormBuilder,
    private acccService: AccountService,
    private principal: Principal,
    private authService: AuthService,
    private router: Router,
    private socketService: SocketService,
    private ytService: YoutubeService,
    private _snackBar: MatSnackBar
  ) {
    this.user = new User();
  }

  ngOnInit() {
    this.prepareYTIframe();
    this.countries = getData();
    this.createNewForm();
    this.watchPrincipal();
  }

  submit() {
    if (!this.updateForm.valid) {
      return;
    }
    this.submitting = true;
    let val = this.updateForm.value;
    this.user.country = val.country;
    this.user.jobInterval = val.jobInterval;

    this.acccService.update(this.user).subscribe((response) => {
      this.submitting = false;
      this.principal.identity(true);
      this.createNewForm();
      this.openSnackBar("User was updated successfully", "");
    }, error => {
      this.submitting = false;
      this.openSnackBar(`Error during user update ${error}`, "");
    });
  }

  getVideoId() {
    if (this.user.ytVideoLink)
      return this.user.ytVideoLink.replace('https://www.youtube.com/watch?v=', '');
  }

  getCommentId() {
    if (this.user.commentLink) {
      let index = this.user.commentLink.indexOf('lc') + 3;
      return this.user.commentLink.substr(index);
    }
  }

  private createNewForm() {
    this.updateForm = this.fb.group({
      country: [this.user.country, Validators.required],
      jobInterval: [this.user.jobInterval, [Validators.required, Validators.min(1), Validators.max(60), Validators.pattern("\\+?\\d+")]],
    })
  }

  private openSnackBar(message: string, action: string) {
    this._snackBar.open(message, action, {
      duration: 2000,
    });
  }


  private watchPrincipal() {
    this.principal.getAuthenticationState().subscribe(res => {
      this.user = res;
      this.createNewForm();
    });
    this.principal.identity(false).then(res => {
      this.user = res;
      this.createNewForm();
      this.socketService.connect(this.user.id);
      this.watchMessages();
      this.getCommentText(this.getCommentId())
    });
  }

  private prepareYTIframe() {

    if (!this.apiLoaded) {
      // This code loads the IFrame Player API code asynchronously, according to the instructions at
      // https://developers.google.com/youtube/iframe_api_reference#Getting_Started
      const tag = document.createElement('script');
      tag.src = 'https://www.youtube.com/iframe_api';
      document.body.appendChild(tag);
      this.apiLoaded = true;
    }
  }

  private watchMessages() {
    this.socketService.notifications$.subscribe(res => {
      if (res) {
        this.user.ytVideoLink = res.videoLink;
        this.user.commentLink = res.commentLink;
        this.getCommentText(this.getCommentId())
      }
    })
  }

  private getCommentText(id: string) {
    this.ytService.getComment(id).subscribe(res => {
      this.commentText = res?.items[0]?.snippet.textDisplay;
    })
  }

  logout() {
    this.authService.logout();
  }
}
