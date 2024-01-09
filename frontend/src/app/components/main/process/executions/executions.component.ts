import { AfterViewInit, Component, OnChanges, OnInit, SimpleChanges, ViewChild } from '@angular/core';
import { CaseExecution } from '../../../../model/caseExecution';
import { Execution } from '../../../../model/execution';
import { DataService } from '../../../../services/data.service';
import RFB from "../../../../../../node_modules/@novnc/novnc/core/rfb.js";
import Swal from 'sweetalert2';
import { Process } from '../../../../model/process';
import { Tms } from '../../../../utils/tms';
import { ApiResponse } from 'src/app/model/apiResponse';
import { Page } from 'src/app/model/page';
import { catchError, startWith } from 'rxjs/operators';
import { HttpErrorResponse } from '@angular/common/http';
import { BehaviorSubject } from 'rxjs';

@Component({
  selector: 'app-executions',
  templateUrl: './executions.component.html',
  styleUrls: ['./executions.component.css']
})
export class ExecutionsComponent implements OnInit, AfterViewInit, OnChanges {

  caseExecutionSelected: CaseExecution;
  caseExecutionVideoSelected: CaseExecution;
  loading: boolean = false;
  modalMaximized: boolean = false;
  selenoidUiHostAndPort: string;
  rfb: RFB; // Vídeo streaming
  ngClass: string;
  showCapabilities: boolean = false;
  resolutions: Map<string, string> = new Map<string, string>(); // <"widthAndHeight", "name">
  newExecution: Execution = new Execution();
  
  currentPage: number = 0;
  pageSize = 10;

  constructor(
    public dataService: DataService, 
    public tms: Tms) {
  }

  ngOnInit(): void {
    console.log("init ExecutionsComponent");
    this.getProcessExecutions();
    this.dataService.processSelectedChange.subscribe(
      value => {
        console.log("new process selected");
        this.hideCaseExecutionDetails();
      });
    this.getSelenoidUiHostAndPort();
    this.getResolutions();
  }

  ngAfterViewInit(): void {
    console.log("ngAfterViewInit");
  }

  ngOnChanges(changes: SimpleChanges) {
    console.log("onChanges ExecutionsComponent");
    // changes.prop contains the old and the new value...
  }

  showCaseExecutionDetails(caseExecution: CaseExecution) {
    this.caseExecutionSelected = caseExecution;
  }

  hideCaseExecutionDetails() {
    this.caseExecutionSelected = new CaseExecution();
  }

  editCapabilities() {
    this.showCapabilities = true;
  }

  closeCapabilities() {
    this.showCapabilities = false;
  }

  getProcessExecutions() {
    this.loading = true;
    this.dataService.getExecutionsPageable(0, this.pageSize).subscribe(
      (executions) => {
        this.dataService.processExecutions = executions;
      },
      (error) => {
        console.error("Error loading executions: " + error.message);
      },
      () => {
        this.loading = false;
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

  maximizeModal() {
    this.modalMaximized = !this.modalMaximized;
  }

  startVideoStreaming(caseExecution: CaseExecution) {
    console.log("startVideoStreaming on " + this.selenoidUiHostAndPort);
    this.caseExecutionVideoSelected = caseExecution;
    var password = "selenoid";

    let protocol;
    if (window.location.protocol === "https:") {
      protocol = 'wss://';
    } else {
      protocol = 'ws://';
    }

    // Create a new RFB object will start a new connection
    this.rfb = new RFB(document.getElementById("screen"), protocol + this.selenoidUiHostAndPort + "/ws/vnc/" + caseExecution.selenoidId, {
      credentials: { password: password },
    });
    this.resizeVnc();
  }

  resizeVnc() {
    if (this.rfb) {
      this.rfb.viewOnly = true;
      //this.rfb.resizeSession = true;
      //this.rfb.scaleViewport = true;
    }
  }

  closeModal() {
    this.rfb.disconnect();
  }

  reload() {
    this.getProcessExecutions();
  }

  createExecution(execution: Execution){
    this.dataService.createExecution(execution).subscribe(
      () => {
        this.reload();
      },
      (error) => {
        console.log(error);
        Swal.fire({
          title: '',
          text: error.error,
          icon: 'error'
        });
        this.loading = false;
      }
    );
  }

  prepareRerunCase(execution: Execution, caseExecution: CaseExecution): void  {
    this.newExecution = new Execution();
    this.newExecution.process = new Process();
    this.newExecution.process.processId = this.dataService.processSelected.processId;
    this.newExecution.configuration = execution.configuration;
    this.newExecution.name = execution.name;
    const reducedExecution = new CaseExecution();
    reducedExecution.cas = caseExecution.cas;
    reducedExecution.screenResolution = caseExecution.screenResolution;
    reducedExecution.browser = caseExecution.browser;
    this.newExecution.casesExecutions = [reducedExecution];
  }

  rerunCase(execution: Execution, caseExecution: CaseExecution): void {
    this.prepareRerunCase(execution, caseExecution);
    this.createExecution(this.newExecution);
  }

  prepareRerunCases(execution: Execution, status: string): void {
    this.newExecution = new Execution();
    this.newExecution.process = new Process();
    this.newExecution.process.processId = this.dataService.processSelected.processId;
    this.newExecution.configuration = execution.configuration;
    this.newExecution.name = execution.name;
    this.newExecution.casesExecutions = execution.casesExecutions
      .filter(caseExecution => {
        if (status === 'ALL') {
          return true;
        }
        return caseExecution.status === status;
      })
      .map(caseExecution => {
        const reducedExecution = new CaseExecution();
        reducedExecution.cas = caseExecution.cas;
        reducedExecution.screenResolution = caseExecution.screenResolution;
        reducedExecution.browser = caseExecution.browser;
        return reducedExecution;
      });
  }

  rerunCases(execution: Execution, status: string): void {
    this.prepareRerunCases(execution, status);
    this.createExecution(this.newExecution);
  }

  rerunTmsModal() {
    this.createExecution(this.newExecution);
  }

  getSelenoidUiHostAndPort() {
    this.dataService.getSelenoidUiHostAndPort().subscribe(
      hostAndPort => {
        this.selenoidUiHostAndPort = hostAndPort;
        console.log("getSelenoidUiHostAndPort -> " + this.selenoidUiHostAndPort);
      }
    );
  }

  getNumberOfTableColumns(): number {
    return this.dataService.isWebProcess() ? 12 : 10;
  }

  getResolutions(): void {
    this.dataService.getResolutions(this.dataService.processSelected.processId).subscribe(
      resolutions => {
        this.resolutions = new Map(resolutions.map(resolution => [resolution.width + 'x' +resolution.height, resolution.name]));
      }
    );
  }

  getScreenResolutionName(screenResolution: string): string {
    const resolutionName = this.resolutions.get(screenResolution);
    if (resolutionName && resolutionName !== ''){
      return resolutionName;
    } else {
      return screenResolution;
    }
  }

  goToPage(pageNumber: number): void {
    this.dataService.getExecutionsPageable(pageNumber, this.pageSize).subscribe(
      executions => {
        this.dataService.processExecutions = executions;
        this.currentPage = pageNumber;
      }
    )
  };

  disableTmsSubmit(){
    if (this.newExecution.tmsCreateNewTestExecution && !this.newExecution.tmsTestExecutionSummary){
      return true;
    }
    if (!this.newExecution.tmsCreateNewTestExecution && !this.newExecution.tmsTestExecutionId){
      return true;
    }
    return false;
  }
}
