import { Component, OnInit } from '@angular/core';
import {ProcessSchedule} from '../../../../../model/processSchedule';
import {DataService} from '../../../../../services/data.service';
import {TranslateService} from '@ngx-translate/core';
import {Execution} from '../../../../../model/excution';
import {CaseExecution} from '../../../../../model/caseExecution';
import {Process} from '../../../../../model/process';
import {Case} from '../../../../../model/case';
import {IDropdownSettings} from 'ng-multiselect-dropdown';
import {Resolution} from '../../../../../model/resolution';
import {Browser} from '../../../../../model/browser';
import {Tag} from '../../../../../model/tag';

@Component({
  selector: 'app-admin-periodic-executions',
  templateUrl: './admin-periodic-executions.component.html',
  styleUrls: ['./admin-periodic-executions.component.css']
})
export class AdminPeriodicExecutionsComponent implements OnInit {
  updated: boolean;
  periodicMod: boolean;
  executionMod: boolean;
  caseExecutionSelected: CaseExecution;
  caseExecutionVideoSelected: CaseExecution;
  loading: boolean;
  dropdownSettings: IDropdownSettings = {};
  selectDays: string;
  processScheduled: ProcessSchedule[];
  processScheduledSelected: ProcessSchedule;
  executions: Map<number, Execution>;
  browsersSelected: Map<number, string[]>;
  resolutionsSelected: Map<number, string[]>;
  casesSelected: Map<number, string[]>;
  execution: Execution;
  periodicDays: any[];
  selectedDays: string[];
  periodicHour: string;
  maxExec: number;
  error: string;
  massiveSelected: boolean;
  tags: Tag[] = [];
  selectedTags: Tag[] = [];
  showPeriodic: boolean;
  showExecution: boolean;
  resolutions: Resolution[];
  selectedResolutions: Array<Resolution>;
  dropdownSettingsReso: IDropdownSettings = {};
  selectResolution: string;
  browsers: Browser[];
  searchString: any;
  constructor(public dataService: DataService, private translate: TranslateService) {
    this.loading = false;
    this.error = '';
  }

  ngOnInit(): void {
    this.dataService.getProcessConfigurations();
    this.getProcessScheduled();
    this.getResolutions();
    this.getBrowsers();
    this.getTags();
    this.updated = false;
    this.executionMod = false;
    this.periodicMod = false;
    this.showPeriodic = false;
    this.showExecution = false;
    this.executions = new Map<number, Execution>();
    this.browsersSelected  = new Map<number, string[]>();
    this.resolutionsSelected  = new Map<number, string[]>();
    this.casesSelected = new Map<number, string[]>();
    this.massiveSelected = false;
    this.selectResolution = this.translate.instant('main.process.trigger.SelectResolutions');
    this.dropdownSettingsReso = {
      idField: 'resolutionId',
      textField: 'widthAndHeight',
      noDataAvailablePlaceholderText: this.translate.instant('main.process.trigger.noResolutionsAvailable'),
      enableCheckAll: true,
      selectAllText: this.translate.instant('main.process.trigger.selectAllResolutions'),
      unSelectAllText: this.translate.instant('main.process.trigger.unselectAllResolutions')
    };
    this.dropdownSettings = {
      idField: 'cronDay',
      textField: 'name',
      noDataAvailablePlaceholderText: this.translate.instant('main.process.trigger.noResolutionsAvailable'),
      enableCheckAll: true,
      selectAllText: this.translate.instant('main.process.trigger.selectAllResolutions'),
      unSelectAllText: this.translate.instant('main.process.trigger.unselectAllResolutions')
    };
    this.selectDays = this.translate.instant('main.process.processAdmin.periodicExecutions.selectDays');
    this.processScheduledSelected = new ProcessSchedule(0, new Process(), 0, '', new Date(), '', 0,5);
    this.periodicDays = [
      {
        name: this.translate.instant('main.process.trigger.week.monday.name'),
        cronDay: this.translate.instant('main.process.trigger.week.monday.cronDay')
      },
      {
        name: this.translate.instant('main.process.trigger.week.tuesday.name'),
        cronDay: this.translate.instant('main.process.trigger.week.tuesday.cronDay'),
      },
      {
        name: this.translate.instant('main.process.trigger.week.wednesday.name'),
        cronDay: this.translate.instant('main.process.trigger.week.wednesday.cronDay')
      },
      {
        name: this.translate.instant('main.process.trigger.week.thursday.name'),
        cronDay: this.translate.instant('main.process.trigger.week.thursday.cronDay')
      },
      {
        name: this.translate.instant('main.process.trigger.week.friday.name'),
        cronDay: this.translate.instant('main.process.trigger.week.friday.cronDay')
      },
      {
        name: this.translate.instant('main.process.trigger.week.saturday.name'),
        cronDay: this.translate.instant('main.process.trigger.week.saturday.cronDay')
      },
      {
        name: this.translate.instant('main.process.trigger.week.sunday.name'),
        cronDay: this.translate.instant('main.process.trigger.week.sunday.cronDay')
      }
    ];
    this.selectedDays = [];
    this.periodicHour = '00:00';
    this.selectedResolutions = [];
  }
  getProcessScheduled(): any{
    this.loading = true;
    this.dataService.getPeriodicExecutions(this.dataService.processSelected.processId).subscribe(
      (processScheduled) => {
      this.processScheduled = processScheduled;
      },
      (error) => {
        console.error('Error loading periodic executions: ' + error.message);
        },
      () => {
        this.loading = false;
      });
  }
  getExecution(processScheduleId: number): any{
    if  (!this.executions.has(processScheduleId)){
      this.dataService.getExecutionByProcessScheduleId(processScheduleId).subscribe(execution => {
          this.executions.set(processScheduleId, execution[0]);
          this.execution = execution[0];
          this.getExecutionData(processScheduleId);
        },
        (error) => {
          console.error('Error loading executions from processSchedule ' + processScheduleId + ': ' + error.message);
        });
    } else {
      this.execution = this.executions.get(processScheduleId);
      this.getExecutionData(processScheduleId);
    }
  }
  getTags(): void {
    this.dataService.getTags(String(this.dataService.processSelected.processId)).subscribe(
      (res: any) => {
        this.tags = res;
      });
  }
  reload(): any {
    this.getProcessScheduled();
  }
  getCaseList(execution: Execution): any {
    const caseNames = new Set();

    execution.casesExecutions.forEach(caseExecution => {
      caseNames.add(caseExecution.cas.name);
    });

    return caseNames;
  }
  getResolutions(): void {
    this.dataService.getResolutions(String(this.dataService.processSelected.processId)).subscribe(
      resolutions => {
        this.resolutions = resolutions;
        this.resolutions.forEach(res => {

          if (res.name && res.name !== ''){
            res.widthAndHeight = res.name;
          } else {
            res.widthAndHeight = res.width + 'x' + res.height;
          }
        });
      }
    );
  }
  getBrowsers(): void {
    this.dataService.getBrowsers().subscribe(
      browsers => {
        this.browsers = browsers;
      }
    );
  }
  getHour(cronExp: string): string {
    const splitCron = cronExp.split(' ');
    return splitCron[2] + ':' + splitCron[1];
  }
  getDays(cronExp: string): string {
    const splitCron = cronExp.split(' ');
    let days = '';
    const splitDays = splitCron[5].split(',');
    if (splitDays.length === 0) {
      return splitCron[5];
    } else {
      splitDays.forEach((day: string) => {
        const transDay = this.translate.instant('main.process.processAdmin.periodicExecutions.week.' + day);
        days = days.concat(transDay + ', ');
      });
    }
    return days.substring(0, days.length - 2);
  }
  changeEdit(index: number): void {
    this.executionMod = false;
    this.periodicMod = false;
    console.log(this.processScheduled[index]);
    console.log('------------------------------------------');
    this.processScheduledSelected = this.processScheduled[index];
    console.log(this.processScheduled[index]);
    this.dataService.processCases.forEach(cd =>  {
      cd.selected = false;
    });
    this.browsers.forEach(br =>  {
      br.selected = false;
    });
    this.selectedResolutions  =  [];
    this.browsersSelected = new Map<number, string[]>();
    this.massiveSelected = false;
    this.getExecution(this.processScheduledSelected.processScheduleId);
    const splitCron = this.processScheduledSelected.cronExpression.split(' ');
    const sd = splitCron[5].split(',');
    this.periodicHour = this.getHour(this.processScheduledSelected.cronExpression);
    this.selectedDays = [];
    sd.forEach(day => {
      const data = this.periodicDays.find(d => d.cronDay === day);
      this.selectedDays.push(data);
    });
    this.showPeriodic = true;
  }
  getExecutionData(processScheduleId: number): void{
    this.executions.get(processScheduleId).casesExecutions.forEach(cs => {
      const index = this.browsers.findIndex(br => br.browserName  ===  cs.browser.browserName);
      this.browsers[index].selected = true;
      if (this.selectedResolutions.findIndex(rs => rs.widthAndHeight  === cs.screenResolution) === -1){
        const  data = this.resolutions.find(rs => rs.widthAndHeight === cs.screenResolution);
        this.selectedResolutions.push(data);
      }
      this.dataService.processCases.find(c => c.caseId ===  cs.cas.caseId).selected =  true;
    });
  }
  massiveChangeState(): void {
    if (!this.massiveSelected) {
      this.dataService.processCases.forEach(pc => {
        pc.selected = false;
      });
      this.selectedTags.splice(0,  this.selectedTags.length);
    } else {
      this.dataService.processCases.forEach(pc => {
        pc.selected = true;
      });
      this.selectedTags.concat(this.tags);
    }
  }
  onCaseSelection(cs: Case): void{}
  changeShowPeriodic(): void{
    if  (this.processScheduledSelected.executionId !== 0) {
      this.showPeriodic = this.showPeriodic !== true;
    }
  }
  changeShowExecution(): void{
    if  (this.processScheduledSelected.executionId !== 0) {
      if (this.showExecution === true) {
        this.showExecution = false;
      } else {
        this.showExecution = true;
      }
    }
  }
  onTagClick(tag: Tag): void  {
    if (this.selectedTags.some(e => e.name === tag.name)){// Is selected
      this.dataService.processCases.forEach(pc => {
        if (pc.clientTags.some(e => e.name === tag.name)){
          if (pc.clientTags.length === 1) { // if process only have one tag
            pc.selected = false;
          } else {// if process have multiple tags
            if (this.selectedTags.length === 1) { // if only have selected one tag
              pc.selected = false;
            } else { // if process have multiple tags and have selected more than one
              let unselect = true;
              pc.clientTags.forEach(ct => {
                if (ct.name !== tag.name){
                  if (this.selectedTags.some(e => e.name === ct.name)){
                    unselect = false;
                  }
                }
              });
              unselect ? pc.selected = false : pc.selected = true;
            }
          }
        }
      });
      this.selectedTags.splice(this.selectedTags.indexOf(tag), 1);
    } else {
      this.dataService.processCases.forEach(pc => {
        if (pc.clientTags.some(e => e.name === tag.name)) {
          pc.selected = true;
        }
      });
      this.selectedTags.push(tag);
    }
  }
  show(): void {
    console.log(this.selectedResolutions);
  }
  invalidForm(): boolean {
    return (
      !this.thereAreCasesSelected()
      || this.dataService.processCases.length === 0
      || !this.processScheduledSelected.name
      || (this.dataService.processSelected.processType.name === 'BROWSER' && !this.thereAreBrowsersSelected())
      || !this.execution.configuration.configurationId
      || this.loading
      || (this.periodicMod === false && this.executionMod === false)
    );
  }
  thereAreCasesSelected(): boolean {
    for (const item of this.dataService.processCases) {
      if (item.selected){
        return true;
      }
    }
    return false;
  }
  thereAreBrowsersSelected(): boolean {
    for (const item of this.browsers) {
      if (item.selected){
        return true;
      }
    }
    return false;
  }
  editPeriodic(): void {
    const splitHour = this.periodicHour.split(':');
    const days = this.selectedDays.map((elem: any) => elem.cronDay).join(',');
    const cronExp = '0 ' + splitHour[1] + ' ' + splitHour[0] + ' ? * ' + days;
    this.loading = true;
    this.updated = false;
    this.error = '';
    this.execution.casesExecutions = [];
    this.dataService.processCases.forEach(item1 => {
      if (item1.selected) {
        // PROCESS OF TYPE 'BROWSER'
        if (this.dataService.processSelected.processType.name === 'BROWSER') {
          for (const item of this.browsers) {
            if (item.selected){
              this.selectedResolutions.forEach(res => {
                const caseExecution = new CaseExecution();
                caseExecution.browser = item;
                caseExecution.cas = item1;
                caseExecution.screenResolution = res.widthAndHeight;
                this.execution.casesExecutions.push(caseExecution);
              });
            }
          }
        }
        // PROCESSES OF OTHER TYPES
        else {
          const caseExecution = new CaseExecution();
          const browser: Browser = new Browser();
          browser.browserName = 'NULLBROWSER';
          caseExecution.browser = browser;
          caseExecution.cas = item1;
          this.execution.casesExecutions.push(caseExecution);
        }
      }
    });
    if (this.periodicMod){
      this.processScheduledSelected.cronExpression = cronExp;
      console.log(this.processScheduledSelected);
      this.execution.name = this.processScheduledSelected.name;
      this.processScheduledSelected.process =  this.dataService.processSelected;

      this.dataService.setPeriodicExecution(this.processScheduledSelected).subscribe(() => {
        this.periodicMod = false;
        },
        (error) => {
          this.error = error.error;
          this.loading = false;
        });
    }
    if (this.executionMod){
      this.execution.process = this.dataService.processSelected;
      console.log(this.execution);
      this.dataService.updateExecution(this.execution).subscribe(() => {
        this.executionMod = false;
        },
        (error) => {
          this.error = error.error;
          this.loading = false;
        });
    }
    this.loading = false;
    this.updated = true;
  }
  changeActive(): void{
    this.periodicMod  = true;
    if (this.processScheduledSelected.active) {
      this.processScheduledSelected.active  =  false;
    }
    else {
      this.processScheduledSelected.active = true;
    }
  }
}
