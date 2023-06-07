import { Component, OnInit } from '@angular/core';
import { DataService } from '../../../services/data.service';
import { PeriodicExecution } from '../../../model/periodic-execution';

@Component({
  selector: 'app-scheduler',
  templateUrl: './scheduler.component.html',
  styleUrls: ['./scheduler.component.css']
})
export class SchedulerComponent implements OnInit {

  periodicExecutions: PeriodicExecution[] = [];

  constructor(public dataService: DataService,) { }

  ngOnInit(): void {
    this.getPeriodicExecutions();
  }

  getPeriodicExecutions(): void {
    this.dataService.getPeriodicExecutions().subscribe(
      periodicExecutions => {
        this.periodicExecutions = periodicExecutions;
      }
    );
  }

}
