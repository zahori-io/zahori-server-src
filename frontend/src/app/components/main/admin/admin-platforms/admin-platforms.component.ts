import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-admin-platforms',
  templateUrl: './admin-platforms.component.html',
  styleUrls: ['./admin-platforms.component.css']
})
export class AdminPlatformsComponent implements OnInit {

  showCapabilities: boolean = false;

  constructor() { }

  ngOnInit(): void {
  }

  editCapabilities() {
    this.showCapabilities = true;
  }

  closeCapabilities() {
    this.showCapabilities = false;
  }

}
