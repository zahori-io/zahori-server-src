import { Component, OnInit } from '@angular/core';
import { Location } from '@angular/common';
import { BannerOptions } from '../utils/banner/banner';

const SUCCESS_COLOR = 'alert alert-success';
const ERROR_COLOR = 'alert alert-danger';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  banner: BannerOptions;
  
  constructor(
    private location: Location
  ) { }

  ngOnInit(): void {
    this.initBanner();
  }

  onActivate(event: any): void {
    let routerOutletComponent: object = event;
    let routerOutletComponentClass: string = event.constructor.name;
    console.log("Activated by: ", routerOutletComponentClass);
    
    this.displayState();
  }

  displayState() {
    let state: any = this.location.getState();

    if (state.message && state.message != "") {
      this.banner = new BannerOptions('', state.message, SUCCESS_COLOR, true);
    }
    if (state.error && state.error != "") {
      this.banner = new BannerOptions('', state.error, ERROR_COLOR, true);
    }
  }

  initBanner() {
    this.banner = new BannerOptions();
  }
}
