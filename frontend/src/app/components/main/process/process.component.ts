import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { DataService } from '../../../services/data.service';
import { ViewEncapsulation } from '@angular/core';

@Component({
  selector: 'app-process',
  templateUrl: './process.component.html',
  styleUrls: ['./process.component.css']
})
export class ProcessComponent implements OnInit {

  hiddenMenu: boolean = true;

  constructor(
    public dataService: DataService,
    private router: Router
  ) { }

  ngOnInit(): void {
    console.log("init process");

    // navigate to dashboard if there is no process selected
    if (!this.dataService.processSelected || !this.dataService.processSelected.processId) {
      this.router.navigate(['/app/dashboard']);
      return;
    }
  }

  showHideMenu() {
    this.hiddenMenu = !this.hiddenMenu;
  }

  processSelection() {
    this.hiddenMenu = true;
  }

}
