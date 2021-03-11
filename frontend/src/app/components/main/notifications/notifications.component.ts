import { Component, OnInit } from '@angular/core';
import { DataService } from '../../../services/data.service';

@Component({
  selector: 'app-notifications',
  templateUrl: './notifications.component.html',
  styleUrls: ['./notifications.component.css']
})
export class NotificationsComponent implements OnInit {

  constructor(
    public dataService: DataService
  ) { }

  ngOnInit(): void {
  }

  closeWindow() {
    this.dataService.showNotificationsWindow = false;
  }
}
