import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Browser } from '../../../../model/browser';
import { Case } from '../../../../model/case';
import { CaseExecution } from '../../../../model/caseExecution';
import { Configuration } from '../../../../model/configuration';
import { Execution } from '../../../../model/excution';
import { Process } from '../../../../model/process';
import { DataService } from '../../../../services/data.service';
import { ViewEncapsulation } from '@angular/core';
import { Tag } from '../../../../model/tag';


@Component({
  selector: 'app-trigger',
  templateUrl: './trigger.component.html',
  styleUrls: ['./trigger.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class TriggerComponent implements OnInit {

  error: string;
  loading: boolean;
  created: boolean;
  browsers: Browser[];
  execution: Execution;
  browsersSelected: Browser[] = [];
  periodicExecutionEnabled: boolean = false;

  tags : Tag[] = [];

  constructor(
    public dataService: DataService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.dataService.getProcessCases();
    this.getBrowsers();
    this.getTags()
    this.newExecution();
    this.error = "";
    this.loading = false;
    this.created = false;
  }

  getTags(){
    this.dataService.getTags(String(this.dataService.processSelected.processId)).subscribe(
      (res : any) => {
        this.tags = res;
      });
  }

  getBrowsers() {
    this.dataService.getBrowsers().subscribe(
      browsers => {
        this.browsers = browsers;
      }
    );
  }

  newExecution() {
    this.execution = new Execution();
    this.execution.configuration = new Configuration();
    this.execution.process = new Process();
    this.execution.process.processId = this.dataService.processSelected.processId;
    this.execution.casesExecutions = [];
  }

  selectCase(processCase: Case, event: any) {
    event.currentTarget.checked ? processCase.selected = true : processCase.selected = false;
  }

  deselectCase(processCase: Case) {
    processCase.selected = false;
  }

  selectBrowser(browser: Browser, event: any) {
    if (event.currentTarget.checked) {
      this.addToBrowserList(browser);
    } else {
      this.removeFromBrowserList(browser);
    }
  }

  addToBrowserList(browser: Browser) {
    if (this.browsersSelected.length == 0) {
      this.browsersSelected.push(browser);
      return;
    }
    for (var i = 0; i < this.browsersSelected.length; i++) {
      if (this.browsersSelected[i].browserName !== browser.browserName) {
        this.browsersSelected.push(browser);
        return;
      }
    }
  }

  removeFromBrowserList(browser: Browser) {
    for (var i = 0; i < this.browsersSelected.length; i++) {
      if (this.browsersSelected[i].browserName === browser.browserName) {
        this.browsersSelected.splice(i, 1);
        return;
      }
    }
  }

  createExecution() {
    this.loading = true;
    this.created = false;
    this.error = "";

    // Create caseExecutions
    this.execution.casesExecutions = [];
    for (var i = 0; i < this.dataService.processCases.length; i++) {
      if (this.dataService.processCases[i].selected) {

        // PROCESS OF TYPE 'BROWSER'
        if (this.dataService.processSelected.processType.name == "BROWSER") {
          for (var j = 0; j < this.browsersSelected.length; j++) {
            var caseExecution = new CaseExecution();
            caseExecution.browser = this.browsersSelected[j];
            caseExecution.cas = this.dataService.processCases[i];

            this.execution.casesExecutions.push(caseExecution);
          }
        }
        // PROCESSES OF OTHER TYPES
        else {
          var caseExecution = new CaseExecution();
          var browser: Browser = new Browser();
          browser.browserName = "NULLBROWSER";
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
        this.clearSelections();
        this.error = "";
        this.created = true;
        this.loading = false;
      },
      (error) => {
        this.error = error.error;
        this.loading = false;
      }
    );
  }

  clearSelections() {
    for (var i = 0; i < this.dataService.processCases.length; i++) {
      this.dataService.processCases[i].selected = false;
    }
  }

  enablePeriodicExecution(event: any) {
    this.periodicExecutionEnabled = event.currentTarget.checked;
  }
}
