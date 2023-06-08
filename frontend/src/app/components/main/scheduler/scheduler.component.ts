import { Component, OnInit } from '@angular/core';
import { DataService } from '../../../services/data.service';
import { PeriodicExecution } from '../../../model/periodic-execution';
import { Execution } from '../../../model/execution';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-scheduler',
  templateUrl: './scheduler.component.html',
  styleUrls: ['./scheduler.component.css']
})
export class SchedulerComponent implements OnInit {

  executions: Execution[] = [];
  periodicWeekdays: String[] = ["MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"];
  periodicDropdownSettings = {};
  selectWeekdaysPlaceholder: string;

  constructor(
    public dataService: DataService,
    private translate: TranslateService
  ) { }

  ngOnInit(): void {
    this.getPeriodicExecutions();

    this.selectWeekdaysPlaceholder = "Selecciona los días";
    this.periodicDropdownSettings = {
      idField: 'widthAndHeight',
      textField: 'nameToDisplay',
      noDataAvailablePlaceholderText: this.translate.instant('main.process.trigger.noResolutionsAvailable'),
      enableCheckAll: true,
      selectAllText: this.translate.instant('main.process.trigger.selectAllResolutions'),
      unSelectAllText: this.translate.instant('main.process.trigger.unselectAllResolutions')
    };
  }

  getPeriodicExecutions(): void {
    this.dataService.getPeriodicExecutions().subscribe(
      executions => {
        
        /*
        executions.forEach(execution => {
          execution.periodicExecutions.forEach(periodicExecution => {
            // periodicExecution.setDaysArray();
          });
        });
        */

        this.executions = executions;
      }
    );
  }

  getCaseList(execution: Execution): any {
    var caseNames = new Set();

    execution.casesExecutions.forEach(function (caseExecution) {
      caseNames.add(caseExecution.cas.name);
    });

    return caseNames;
  }

  savePeriodicExecutions(){
    this.dataService.savePeriodicExecutions(this.executions).subscribe(
      (executions) => {
        console.log('Executions saved');
        this.executions = executions;
      },
      (error) => {
        // TODO
        //this.banner = new BannerOptions('', this.translate.instant('banner.error') + error.message, ERROR_COLOR, true);
      }
    );
  }
}
