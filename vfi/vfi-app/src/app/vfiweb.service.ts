import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
// import { Observable } from 'rxjs';

interface mySub {
  Object
}

@Injectable({
  providedIn: 'root'
})
export class VfiwebService {
  subData: mySub = { Object};
  baseUrl = 'http://localhost:8090/api/vfi';

  constructor(private http: HttpClient) { }

  installData() {
    return this.http.get('http://localhost:8090/api/vfi/data').subscribe(data => {
      console.log('Got install response', data);
    });
  }

  getSubscribersData() {
    this.http.get<mySub>('http://localhost:8090/api/vfi').subscribe(data => {
      console.log('Got Sub response', data);
      this.subData = data;
    });
    return this.subData;
  }

  getSubscriberData() {
    this.http.get<mySub>('http://localhost:8090/api/vfi/0000002178-18-000067').subscribe(data => {
      console.log('Got Sub response', data);
      this.subData = data;
    });
    return this.subData;
  }

  /*public getSub(adsh: string): Observable<mySub> {
    const url = this.baseUrl + '/' + adsh;
    console.log ('URL = ', url);
    return this.http.get(url).map((json: mySub): sub => {
            return (json[sub'] as sub);
            });
  }*/

  getSubData(adsh: string) {
    const url = this.baseUrl + '/' + adsh;
    console.log ('URL = ', url);
    this.http.get<mySub>(url).subscribe(res => {
        console.log('Got Subscriber response', res);
        this.subData = res;
    });
    return this.subData;
  }

  getRecordData() {
      return [
          {
        name: 'Athi',
        online: true
      },
      {
        name: 'Dheekshaa',
        online: false
      },
      {
        name: 'Saru',
        online: true
      },
      {
        name: 'Vishal',
        online: false
      }
  ];
  }
}
