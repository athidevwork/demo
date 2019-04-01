import { Component } from '@angular/core';
import {ViewChild, ElementRef} from '@angular/core';

import { VfiwebService } from './vfiweb.service';

interface mySub {
  Object;
}

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'vfi-app';
  // records = {};
  response: mySub = { Object };
  subs = [];
  adsh = '';

  // Reference adshInput variable inside Component
  @ViewChild('adshInput') nameInputRef: ElementRef;

  constructor(private vfiwebService: VfiwebService) {

  }

  installData() {
    this.vfiwebService.installData();
  }

  getAllSubs() {
    this.response = this.vfiwebService.getSubscribersData();
  }

  getSub(subAdsh: HTMLInputElement) {
    this.adsh = this.nameInputRef.nativeElement.value + ' ' + subAdsh.value;
    // this.sub = this.vfiwebService.getSubData('0000002178-18-000067');
    console.log('input data: ' + this.adsh);
    this.response = this.vfiwebService.getSubData(this.adsh);
    console.log ('response in getSub component', this.response);
    return this.response;
  }
}
