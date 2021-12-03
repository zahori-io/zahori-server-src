import { AfterViewInit, Component, OnChanges, OnInit, SimpleChanges, ViewChild } from '@angular/core';
import { CaseExecution } from '../../../../model/caseExecution';
import { Execution } from '../../../../model/excution';
import { DataService } from '../../../../services/data.service';
import RFB from '../../../../../../node_modules/@novnc/novnc/core/rfb.js';

@Component({
  selector: 'app-executions',
  templateUrl: './executions.component.html',
  styleUrls: ['./executions.component.css']
})
export class ExecutionsComponent implements OnInit, AfterViewInit, OnChanges {

  caseExecutionSelected: CaseExecution;
  caseExecutionVideoSelected: CaseExecution;
  loading = false;
  modalMaximized = false;
  selenoidUiHostAndPort: string;
  rfb: RFB; // Vídeo streaming
  ngClass: string;
  showCapabilities: boolean = false;

  constructor(public dataService: DataService) {
  }

  ngOnInit(): any {
    console.log('init ExecutionsComponent');
    this.getProcessExecutions();
    this.dataService.processSelectedChange.subscribe(
      value => {
        console.log('new process selected');
        this.hideCaseExecutionDetails();
      });
    this.getSelenoidUiHostAndPort();
  }

  ngAfterViewInit(): any {
    console.log('ngAfterViewInit');
  }

  ngOnChanges(changes: SimpleChanges): any {
    console.log('onChanges ExecutionsComponent');
    // changes.prop contains the old and the new value...
  }

  showCaseExecutionDetails(caseExecution: CaseExecution): any {
    this.caseExecutionSelected = caseExecution;
  }

  hideCaseExecutionDetails(): any {
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

        /*
        this.dataTable = $(this.table.nativeElement);
        this.dataTable.DataTable();
        */

        /*
        $('#dataTableChild').each(function(idx, el){
          console.log("tabla "+idx);
          //here this/el refers to the current image dom reference
          //do soemthing
          var dataTableChild = $(el.nativeElement);
          this.dataTableChild.DataTable();
        })
        */
      },
      (error) => {
        console.error('Error loading executions: ' + error.message);
      },
      () => {
        this.loading = false;
      }
    );
  }

  getCaseList(execution: Execution): any {
    let caseNames = new Set();

    execution.casesExecutions.forEach(function(caseExecution) {
      caseNames.add(caseExecution.cas.name);
    });

    return caseNames;
  }

  getBrowserList(execution: Execution): any {
    const browserNames = new Set();

    execution.casesExecutions.forEach(function(caseExecution) {
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

  startVideoStreaming(caseExecution: CaseExecution): any {
    console.log('startVideoStreaming on ' + this.selenoidUiHostAndPort);
    this.caseExecutionVideoSelected = caseExecution;
    const password = 'selenoid';

    let protocol;
    if (window.location.protocol === 'https:') {
      protocol = 'wss://';
    } else {
      protocol = 'ws://';
    }

    // Create a new RFB object will start a new connection
    this.rfb = new RFB(document.getElementById('screen'), protocol + this.selenoidUiHostAndPort + '/ws/vnc/' + caseExecution.selenoidId, {
      credentials: { password },
    });
    this.resizeVnc();
  }

  resizeVnc(): any {
    if (this.rfb) {
      this.rfb.viewOnly = true;
      // this.rfb.resizeSession = true;
      // this.rfb.scaleViewport = true;
    }
  }

  closeModal(): any {
    this.rfb.disconnect();
  }

  reload(): any {
    this.getProcessExecutions();
  }

  getSelenoidUiHostAndPort(): any {
    this.dataService.getSelenoidUiHostAndPort().subscribe(
      hostAndPort => {
        this.selenoidUiHostAndPort = hostAndPort;
        console.log('getSelenoidUiHostAndPort -> ' + this.selenoidUiHostAndPort);
      }
    );
  }
  
  getNumberOfTableColumns(): number {
    return this.dataService.isWebProcess() ? 11 : 9;
  }

}
