import { AfterViewInit, Component, OnChanges, OnInit, SimpleChanges, ViewChild } from '@angular/core';
import { CaseExecution } from '../../../../model/caseExecution';
import { Execution } from '../../../../model/excution';
import { DataService } from '../../../../services/data.service';
import RFB from "../../../../../../node_modules/@novnc/novnc/core/rfb.js";
import Swal from 'sweetalert2';
import { Process } from '../../../../model/process';

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

  constructor(public dataService: DataService) {
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
    this.dataService.getExecutions().subscribe(
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

  rerunCase(execution: Execution, caseExecution: CaseExecution): void {
    console.log('failed case');
    const newExecution: Execution = new Execution();
    newExecution.process = new Process();
    newExecution.process.processId = this.dataService.processSelected.processId;
    newExecution.configuration = execution.configuration;
    newExecution.name = execution.name;
    const reducedExecution = new CaseExecution();
    reducedExecution.cas = caseExecution.cas;
    reducedExecution.screenResolution = caseExecution.screenResolution;
    reducedExecution.browser = caseExecution.browser;
    newExecution.casesExecutions = [reducedExecution];
    this.dataService.createExecution(newExecution).subscribe(
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

  rerunCases(execution: Execution, status: string): void {
    const newExecution: Execution = new Execution();
    newExecution.process = new Process();
    newExecution.process.processId = this.dataService.processSelected.processId;
    newExecution.casesExecutions = execution.casesExecutions
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
    newExecution.configuration = execution.configuration;
    newExecution.name = execution.name;
    if (newExecution.casesExecutions.length > 0) {
      this.dataService.createExecution(newExecution).subscribe(
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
    } else {
      Swal.fire({
        title: '',
        text: 'There are no cases to be executed!',
        icon: 'warning'
      });
      this.loading = false;
    }
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

}
