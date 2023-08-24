import { Component, OnInit } from '@angular/core';
import { Process } from '../../../model/process';
import { DataService } from '../../../services/data.service';
import { Execution } from '../../../model/execution';
import { CaseExecution } from '../../../model/caseExecution';
import { ExecutionStats } from '../../../model/executionStats';
import { BrowserExecutionStats } from '../../../model/browserExecutionsStats';
import { ServerVersions } from '../../../model/serverVersions';
import { TranslateService } from '@ngx-translate/core';
import { Router } from '@angular/router';
import { AccountChangeEmailComponent } from '../account/account-change-email/account-change-email.component';

const SUCCESS_COLOR = 'alert alert-success';
@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {

  processes: Process[];
  serverVersions: ServerVersions;

  // modal to ask the user to introduce the email if not present in DB.
  titleModal: string;
  componentModal: any;
  displayModal = false;
  dataModal: any;

  constructor(
    public dataService: DataService, 
    private translate: TranslateService,
    private router: Router) {
  }

  ngOnInit(): void {
    this.getClient();
    this.getLastServerVersion();
    this.askForEmail();
  }

  getClient(): void {
    this.dataService.getClient().subscribe(
      client => {
        this.dataService.client = client;
        this.dataService.setFirstTeam();
        for (let team of this.dataService.client.clientTeams) {
          for (let process of team.processes) {
            this.setProcessLastExecutionStats(process);
          }
        }
      }
    );
  }

  setProcessLastExecutionStats(process: Process) {
    this.dataService.getLastExecution(process.processId).subscribe(
      (lastExecutionFromDb) => {
        process
        const lastExecutionStats: ExecutionStats = this.calculateStats(lastExecutionFromDb.data.content[0]);
        process.lastExecutionStats = lastExecutionStats;
      },
      (error) => {
        console.error("Error loading last execution for process '" + process.processId + "': " + error.message);
      }
    );
  }

  calculateStats(execution: Execution): ExecutionStats {
    const lastExecutionStats: ExecutionStats = new ExecutionStats();

    // TODO validar que la ejecución no este Running
    lastExecutionStats.totalPassed = execution.totalPassed;
    lastExecutionStats.totalFailed = execution.totalFailed;

    for (let i = 0; i < execution.casesExecutions.length; i++) {
      const caseExecution: CaseExecution = execution.casesExecutions[i];
      let browserExecutionStats: BrowserExecutionStats = lastExecutionStats.browserStats.get(caseExecution.browser.browserName);
      if (!browserExecutionStats) {
        browserExecutionStats = new BrowserExecutionStats();
        lastExecutionStats.browserStats.set(caseExecution.browser.browserName, browserExecutionStats);
      }

      if ('PASSED' === caseExecution.status) {
        browserExecutionStats.totalPassed += 1;
      }
      if ('FAILED' === caseExecution.status) {
        browserExecutionStats.totalFailed += 1;
      }
      if ('Not executed' === caseExecution.status) {
        browserExecutionStats.totalFailed += 1;
      }
    }

    lastExecutionStats.percent = this.getPercent(lastExecutionStats);
    return lastExecutionStats;
  }

  getPartsPerUnit(lastExecutionStats: ExecutionStats): number {
    return this.getNumberMaxTwoDecimals(lastExecutionStats.totalPassed / (lastExecutionStats.totalPassed + lastExecutionStats.totalFailed));
  }

  getPercent(lastExecutionStats: ExecutionStats): number {
    if (lastExecutionStats.totalPassed + lastExecutionStats.totalFailed > 0) {
      return this.getPartsPerUnit(lastExecutionStats) * 100;
    } else {
      return 0;
    }
  }

  getBrowserPartsPerUnit(browserExecutionStats: BrowserExecutionStats): number {
    return this.getNumberMaxTwoDecimals(browserExecutionStats.totalPassed / (browserExecutionStats.totalPassed + browserExecutionStats.totalFailed));
  }

  getBrowserPercent(browserExecutionStats: BrowserExecutionStats): number {
    return this.getBrowserPartsPerUnit(browserExecutionStats) * 100;
  }

  getNumberMaxTwoDecimals(percent: number): number {
    // Round parts per unit number (i.e.: 0.50): toFixed(n)  n=2 -> No decimal, n=3 -> one decimal
    return parseFloat(percent.toFixed(2));
  }

  getLastServerVersion() {
      const versionsVariableNameInStorage = 'serverVersions';

      // Get latests versions from local storage
      let serverVersionsInSession = localStorage.getItem(versionsVariableNameInStorage);
      if (serverVersionsInSession){
        this.serverVersions = JSON.parse(serverVersionsInSession);
      } else {
        // Get latests versions from server
        this.dataService.getServerVersions().subscribe(
          (serverVersions) => {
            this.serverVersions = serverVersions;
            localStorage.setItem(versionsVariableNameInStorage, JSON.stringify(serverVersions));
          },
          (error) => {
            console.error('Error getting server versions: ' + error.message);
          }
        );
      }
  }

  askForEmail() {
    this.dataService.getEmailServiceStatus().subscribe(
      // email service is configured
      () => {
        this.dataService.getEmail().subscribe(
          (emailDto) => {
            // user has an email pending to be verified
            if (emailDto.newEmail && emailDto.newEmail != ''){
              return;
            }
            // user has no email, open modal to ask for it
            if (!emailDto.email || emailDto.email == ''){
              this.componentModal = AccountChangeEmailComponent;
              this.displayModal = true;
              this.titleModal = this.translate.instant('main.dashboard.askForEmailTitle');
              this.dataModal = {"error": this.translate.instant('main.dashboard.askForEmailAlert')};
            }
          },
          (error) => {
            console.log("Error getting user email: " + error.error);
          }
        );
      },
      // email service is not configured
      (error) => {
        console.log("Email service status: " + error.error);
      }
    );
  }

  onModalCloseEvent() {
    this.displayModal = false;
    // Validate if user has filled the email
    this.askForEmail();
  }
}
