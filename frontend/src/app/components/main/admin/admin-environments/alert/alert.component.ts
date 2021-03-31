import { Component, OnInit, Input } from '@angular/core';
import { AlertOptions } from './alert';

@Component({
  selector: 'boot-alert',
  templateUrl: './alert.component.html',
  styleUrls: ['./alert.component.css']
})
export class AlertComponent {

  @Input() 
  alert: AlertOptions;

  constructor() { }

  ngOnInit(): void {
  }

}
