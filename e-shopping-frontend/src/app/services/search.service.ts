import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class SearchService {

  private searchValue = new BehaviorSubject<string>('');
  search$ = this.searchValue.asObservable();

  setSearch(value: string) {
    this.searchValue.next(value);
  }
}