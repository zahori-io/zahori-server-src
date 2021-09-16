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
  styleUrls: ['./trigger.component.css']
})
export class TriggerComponent implements OnInit {

  error: string;
  loading: boolean;
  created: boolean;
  browsers: Browser[];
  execution: Execution;
  periodicExecutionEnabled: boolean = false;

  tags: Tag[] = [];

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

  getTags() {
    this.dataService.getTags(String(this.dataService.processSelected.processId)).subscribe(
      (res: any) => {
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
    this.clearSelectedBrowsers();
    this.clearSelectedCases();
  }

  clearSelectedBrowsers() {
    if (this.browsers) {
      for (var j = 0; j < this.browsers.length; j++) {
        this.browsers[j].selected = false;
      }
    }
  }

  deselectCase(processCase: Case) {
    processCase.selected = false;
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
          for (var j = 0; j < this.browsers.length; j++) {
            if (this.browsers[j].selected){
              var caseExecution = new CaseExecution();
              caseExecution.browser = this.browsers[j];
              caseExecution.cas = this.dataService.processCases[i];

              this.execution.casesExecutions.push(caseExecution);
            }
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

  invalidForm(): boolean {
    return (
      !this.thereAreCasesSelected()
      || this.dataService.processCases.length == 0
      || !this.execution.name
      || (this.dataService.processSelected.processType.name == 'BROWSER' && !this.thereAreBrowsersSelected())
      || !this.execution.configuration.configurationId
      || this.loading
    );
  }
  
  thereAreCasesSelected() : boolean {
    for (var i = 0; i < this.dataService.processCases.length; i++) {
      if (this.dataService.processCases[i].selected){
        return true;
      }
    }
    return false;
  }

  thereAreBrowsersSelected() : boolean {
    for (var i = 0; i < this.browsers.length; i++) {
      if (this.browsers[i].selected){
        return true;
      }
    }
    return false;
  }

  clearSelectedCases() {
    for (var i = 0; i < this.dataService.processCases.length; i++) {
      this.dataService.processCases[i].selected = false;
    }
  }

  enablePeriodicExecution(event: any) {
    this.periodicExecutionEnabled = event.currentTarget.checked;
  }
}
