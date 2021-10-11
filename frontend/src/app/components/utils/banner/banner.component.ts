import { Component, OnInit, Input } from '@angular/core';
import { BannerOptions } from './banner';

@Component({
  selector: 'banner',
  templateUrl: './banner.component.html',
  styleUrls: ['./banner.component.css']
})
export class BannerComponent {

  @Input()
  banner: BannerOptions;
  show = false;
  constructor() { }
}
