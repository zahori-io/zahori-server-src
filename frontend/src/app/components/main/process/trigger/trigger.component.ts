import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Browser } from '../../../../model/browser';
import { Case } from '../../../../model/case';
import { CaseExecution } from '../../../../model/caseExecution';
import { Configuration } from '../../../../model/configuration';
import { Execution } from '../../../../model/excution';
import { Process } from '../../../../model/process';
import { DataService } from '../../../../services/data.service';
import { Tag } from '../../../../model/tag';
import {Resolution} from '../../../../model/resolution';
import {IDropdownSettings} from 'ng-multiselect-dropdown';
import {TranslateService} from '@ngx-translate/core';


@Component({
  selector: 'app-trigger',
  templateUrl: './trigger.component.html',
  styleUrls: ['./trigger.component.css']
})
export class TriggerComponent implements OnInit {

  error: string;
  loading: boolean;
  created: boolean;
  browsers: Browser[];
  execution: Execution;
  periodicExecutionEnabled = false;
  massiveSelected: boolean;
  tags: Tag[] = [];
  selectedTags: Tag[] = [];
  resolutions: Resolution[];
  selectedResolutions: Array<Resolution>;
  dropdownSettings: IDropdownSettings = {};
  selectResolution: string;

  constructor(public dataService: DataService, private router: Router, private  translate: TranslateService) {
  }

  ngOnInit(): void {
    this.dataService.getProcessCases();
    this.getBrowsers();
    this.getTags();
    this.newExecution();
    this.getResolutions();
    this.error = '';
    this.loading = false;
    this.created = false;
    this.massiveSelected = false;
    this.selectResolution = this.translate.instant('main.process.trigger.SelectResolutions');
    this.dropdownSettings = {
      idField: 'resolutionId',
      textField: 'widthAndHeight',
      noDataAvailablePlaceholderText: this.translate.instant('main.process.trigger.noResolutionsAvailable'),
      enableCheckAll: true,
      selectAllText: this.translate.instant('main.process.trigger.selectAllResolutions'),
      unSelectAllText: this.translate.instant('main.process.trigger.unselectAllResolutions')
    };
  }
  getTags(): void {
    this.dataService.getTags(String(this.dataService.processSelected.processId)).subscribe(
      (res: any) => {
        this.tags = res;
      });
  }
  getBrowsers(): void {
    this.dataService.getBrowsers().subscribe(
      browsers => {
        this.browsers = browsers;
      }
    );
  }
  getResolutions(): void {
    this.dataService.getResolutions(String(this.dataService.processSelected.processId)).subscribe(
      resolutions => {
        this.resolutions = resolutions;
        this.resolutions.forEach(res => {
          res.widthAndHeight = res.width + 'x' + res.height;
        });
      }
    );
  }
  newExecution(): void  {
    this.execution = new Execution();
    this.execution.configuration = new Configuration();
    this.execution.process = new Process();
    this.execution.process.processId = this.dataService.processSelected.processId;
    this.execution.casesExecutions = [];
    this.clearSelectedBrowsers();
    this.clearSelectedCases();
  }

  clearSelectedBrowsers(): void  {
    if (this.browsers) {
      for (let j = 0; j < this.browsers.length; j++) {
        this.browsers[j].selected = false;
      }
    }
  }

  deselectCase(processCase: Case): void  {
    processCase.selected = false;
  }

  createExecution(): void  {
    this.loading = true;
    this.created = false;
    this.error = '';

    // Create caseExecutions
    this.execution.casesExecutions = [];
    for (let i = 0; i < this.dataService.processCases.length; i++) {
      if (this.dataService.processCases[i].selected) {

        // PROCESS OF TYPE 'BROWSER'
        if (this.dataService.processSelected.processType.name === 'BROWSER') {
          for (const item of this.browsers) {
            if (item.selected){
              const caseExecution = new CaseExecution();
              caseExecution.browser = item;
              caseExecution.cas = this.dataService.processCases[i];
              this.selectedResolutions.forEach(res => {
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
          caseExecution.cas = this.dataService.processCases[i];

          this.execution.casesExecutions.push(caseExecution);
        }
      }
    }

    // submit execution to backend
    this.dataService.createExecution(this.execution).subscribe(
      () => {
        this.newExecution();
        this.error = '';
        this.created = true;
        this.loading = false;
      },
      (error) => {
        this.error = error.error;
        this.loading = false;
      }
    );
  }

  invalidForm(): boolean {
    return (
      !this.thereAreCasesSelected()
      || this.dataService.processCases.length === 0
      || !this.execution.name
      || (this.dataService.processSelected.processType.name === 'BROWSER' && !this.thereAreBrowsersSelected())
      || !this.execution.configuration.configurationId
      || this.loading
    );
  }
  thereAreCasesSelected(): boolean {
    for (let i = 0; i < this.dataService.processCases.length; i++) {
      if (this.dataService.processCases[i].selected){
        return true;
      }
    }
    return false;
  }

  thereAreBrowsersSelected(): boolean {
    for (let i = 0; i < this.browsers.length; i++) {
      if (this.browsers[i].selected){
        return true;
      }
    }
    return false;
  }

  clearSelectedCases(): void  {
    for (let i = 0; i < this.dataService.processCases.length; i++) {
      this.dataService.processCases[i].selected = false;
    }
  }

  enablePeriodicExecution(event: any): void  {
    this.periodicExecutionEnabled = event.currentTarget.checked;
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
  onCaseSelection(cs: Case): void{

      const selectTags = new Map(cs.clientTags.map(key => [key.tagId, true]));
      this.dataService.processCases.forEach(pc => {
        if (!pc.selected && pc.clientTags.length > 0){// Solo  casos no seleccionados y que tengan tags
          pc.clientTags.forEach(ptg => {
            if (cs.clientTags.some(e => e.name === ptg.name)){
              selectTags.set(ptg.tagId, false);
            }
          });
        }
      });
      console.log(selectTags);
      selectTags.forEach((value: boolean, key: number) => {
        if (!value && this.selectedTags.some(e => e.tagId === key)){
            this.selectedTags.splice(this.selectedTags.indexOf(this.selectedTags.find(tg => tg.tagId === key)), 1);
        }
        if (value && !this.selectedTags.some(e => e.tagId === key)){
          this.selectedTags.push(this.tags.find(tg => tg.tagId === key));
        }
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
}
