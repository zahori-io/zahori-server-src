import { Component, OnInit } from '@angular/core';
import { DataService } from 'src/app/services/data.service';


@Component({
  selector: 'app-adminprocess-menu',
  templateUrl: './process-admin-menu.component.html',
  styleUrls: ['./process-admin-menu.component.css']
})
export class ProcessAdminMenuComponent implements OnInit {

  constructor(public dataService: DataService) {
  }

  ngOnInit(): void {
    console.log("processadmin --->", this.dataService.processSelected)

  }

}
