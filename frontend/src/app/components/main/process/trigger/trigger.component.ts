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
import {getSortHeaderNotContainedWithinSortError} from '@angular/material/sort/sort-errors';


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

  constructor(
    public dataService: DataService,
    private router: Router
  ) {

  }

  ngOnInit(): void {
    this.dataService.getProcessCases();
    this.getBrowsers();
    this.getTags();
    this.newExecution();
    this.error = '';
    this.loading = false;
    this.created = false;
    this.massiveSelected = false;
  }

  getTags() {
    this.dataService.getTags(String(this.dataService.processSelected.processId)).subscribe(
      (res: any) => {
        this.tags = res;
      });
  }
  getBrowsers () {
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
    this.error = '';

    // Create caseExecutions
    this.execution.casesExecutions = [];
    for (let i = 0; i < this.dataService.processCases.length; i++) {
      if (this.dataService.processCases[i].selected) {

        // PROCESS OF TYPE 'BROWSER'
        if (this.dataService.processSelected.processType.name === 'BROWSER') {
          for (let j = 0; j < this.browsers.length; j++) {
            if (this.browsers[j].selected){
              let caseExecution = new CaseExecution();
              caseExecution.browser = this.browsers[j];
              caseExecution.cas = this.dataService.processCases[i];

              this.execution.casesExecutions.push(caseExecution);
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

  thereAreBrowsersSelected() : boolean {
    for (var i = 0; i < this.browsers.length; i++) {
      if (this.browsers[i].selected){
        return true;
      }
    }
    return false;
  }

  clearSelectedCases() {
    for (let i = 0; i < this.dataService.processCases.length; i++) {
      this.dataService.processCases[i].selected = false;
    }
  }

  enablePeriodicExecution(event: any) {
    this.periodicExecutionEnabled = event.currentTarget.checked;
  }

  onTagClick(tag: Tag) {
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
          console.log('paso por aqui');
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
