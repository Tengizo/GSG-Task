import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";

let BASE_ACC_URL = "https://www.googleapis.com/youtube/v3/comments?key=AIzaSyB5NAw_AeGMPIajOdzDQmrJNG4DqPmD5Pg&part=snippet&id=";

@Injectable({
  providedIn: 'root'
})
export class YoutubeService {

  constructor(private http: HttpClient) {
  }

  getComment(id: string): Observable<any> {
    return this.http.get(BASE_ACC_URL + id);
  }

}
