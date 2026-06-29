import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private api='http://localhost:8080/user-service/auth';

  constructor(private http: HttpClient) {}

  getProfile(): Observable<any>{
      return this.http.get(`${this.api}/me`);
  }

}