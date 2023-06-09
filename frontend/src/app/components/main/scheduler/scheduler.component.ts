import { Component, OnInit } from '@angular/core';
import { DataService } from '../../../services/data.service';
import { PeriodicExecution } from '../../../model/periodic-execution';
import { Execution } from '../../../model/execution';
import { TranslateService } from '@ngx-translate/core';
import Swal from 'sweetalert2';
import { BannerOptions } from '../../../utils/banner/banner';

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
  periodicDropdownSettings = {};
  selectWeekdaysPlaceholder: string;
  banner: BannerOptions;

  constructor(
    public dataService: DataService,
    private translate: TranslateService
  ) { }

  ngOnInit(): void {
    this.banner = new BannerOptions();

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

  savePeriodicExecutions() {
    this.dataService.savePeriodicExecutions(this.executions).subscribe(
      (executions) => {
        console.log('Executions saved');
        this.executions = executions;
        this.banner = new BannerOptions('Programaciones actualizadas', '', SUCCESS_COLOR, true);
      },
      (error) => {
        // TODO i18n
        this.banner = new BannerOptions('Error al guardar las programaciones:', error.message, ERROR_COLOR, true);
      }
    );
  }

  deletePeriodicExecution(execution: Execution, index: number): void {
    Swal.fire({
      title: this.translate.instant('main.process.cases.removeCase.title') + execution.name,
      text: this.translate.instant('main.process.cases.removeCase.warning'),
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: this.translate.instant('main.process.cases.removeCase.confirmButton'),
      cancelButtonText: this.translate.instant('main.process.cases.removeCase.cancelButton'),
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
            this.banner = new BannerOptions('Error al eliminar la ejecución programada:', error.message, ERROR_COLOR, true);
          }, () => {
            this.banner = new BannerOptions('Ejecución programada eliminada', '', SUCCESS_COLOR, true);
          }
        );
      }
    });
  }

  closeBanner(): void {
    this.banner = new BannerOptions();
  }

}
