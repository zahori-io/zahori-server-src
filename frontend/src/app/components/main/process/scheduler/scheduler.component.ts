import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import Swal from 'sweetalert2';
import { BannerOptions } from '../../../utils/banner/banner';
import { DataService } from '../../../../services/data.service';
import { Execution } from '../../../../model/execution';
import { IDropdownSettings } from 'ng-multiselect-dropdown';
import { CaseExecution } from '../../../../model/caseExecution';

const SUCCESS_COLOR = 'alert alert-success';
const ERROR_COLOR = 'alert alert-danger';

@Component({
  selector: 'app-scheduler',
  templateUrl: './scheduler.component.html',
  styleUrls: ['./scheduler.component.css']
})
export class SchedulerComponent implements OnInit {

  executions: Execution[] = [];
  periodicWeekdays: String[] = ["MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"];
  periodicDropdownSettings: IDropdownSettings  = {};
  selectWeekdaysPlaceholder: string;
  banner: BannerOptions;
  resolutions: Map<string, string> = new Map<string, string>(); // <"widthAndHeight", "name">
  
  constructor(
    public dataService: DataService,
    private translate: TranslateService
  ) { }

  ngOnInit(): void {
    this.banner = new BannerOptions();

    this.getPeriodicExecutions();
    this.getResolutions();

    this.selectWeekdaysPlaceholder = this.translate.instant('main.process.scheduler.daysPlaceholder');
    this.periodicDropdownSettings = {
      idField: 'widthAndHeight',
      textField: 'nameToDisplay',
      noDataAvailablePlaceholderText: this.translate.instant('main.process.scheduler.daysNotAvailable'),
      enableCheckAll: true,
      selectAllText: this.translate.instant('main.process.scheduler.daysSelectAll'),
      unSelectAllText: this.translate.instant('main.process.scheduler.daysUnselectAll')
    };
  }

  getPeriodicExecutions(): void {
    this.dataService.getPeriodicExecutions().subscribe(
      executions => {
        this.executions = executions;
      }
    );
  }

  savePeriodicExecutions() {
    this.dataService.savePeriodicExecutions(this.executions).subscribe(
      (executions) => {
        console.log('Schedules saved');
        this.executions = executions;
                    
        this.banner = new BannerOptions(this.translate.instant('main.process.scheduler.saveMessageOk'), '', SUCCESS_COLOR, true);
      },
      (error) => {
        this.banner = new BannerOptions(this.translate.instant('main.process.scheduler.saveMessageKo'), error.message, ERROR_COLOR, true);
      }
    );
  }

  deletePeriodicExecution(execution: Execution, index: number): void {
    Swal.fire({
      title: this.translate.instant('main.process.scheduler.deleteTitle') + execution.name,
      text: this.translate.instant('main.process.scheduler.deleteWarning'),
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: this.translate.instant('main.process.scheduler.deleteConfirmButton'),
      cancelButtonText: this.translate.instant('main.process.scheduler.deleteCancelButton'),
      backdrop: `
            rgba(64, 69, 58,0.4)
            left top
            no-repeat`
    }).then((result) => {
      if (result.value) {
        this.dataService.deletePeriodicExecution(execution).subscribe(
          () => {
            this.executions.splice(index, 1);
          },
          (error) => {
            this.banner = new BannerOptions(this.translate.instant('main.process.scheduler.deleteMessageKo'), error.message, ERROR_COLOR, true);
          }, () => {
            this.banner = new BannerOptions(this.translate.instant('main.process.scheduler.deleteMessageOk'), '', SUCCESS_COLOR, true);
          }
        );
      }
    });
  }

  closeBanner(): void {
    this.banner = new BannerOptions();
  }

  getCaseList(execution: Execution): Set<string> {
    let caseNames = new Set<string>();

    execution.casesExecutions.forEach(function (caseExecution) {
      caseNames.add(caseExecution.cas.name);
    });

    return caseNames;
  }

  getCaseListForTitle(execution: Execution): string {
    let caseNames = "";

    this.getCaseList(execution).forEach(function (caseName) {
      caseNames = caseNames + caseName + "\n";
    });

    return caseNames;
  }

  getBrowserList(execution: Execution): any {
    var browserNames = new Set();

    execution.casesExecutions.forEach(function (caseExecution) {
      browserNames.add(caseExecution.browser.browserName);
    });

    return browserNames;
  }

  getScreenResolutionsList(execution: Execution): any {
    var resolutions = new Set();

    execution.casesExecutions.forEach(function (caseExecution) {
      resolutions.add(caseExecution.screenResolution);
    });

    return resolutions;
  }

  getScreenResolutionName(screenResolution: string): string {
    const resolutionName = this.resolutions.get(screenResolution);
    if (resolutionName && resolutionName !== ''){
      return resolutionName;
    } else {
      return screenResolution;
    }
  }

  getResolutions(): void {
    this.dataService.getResolutions(this.dataService.processSelected.processId).subscribe(
      resolutions => {
        this.resolutions = new Map(resolutions.map(resolution => [resolution.width + 'x' +resolution.height, resolution.name]));
      }
    );
  }
  
}
