import { Component, OnInit } from '@angular/core';
import { Process } from '../../../model/process';
import { DataService } from '../../../services/data.service';
import { Execution } from '../../../model/execution';
import { CaseExecution } from '../../../model/caseExecution';
import { ExecutionStats } from '../../../model/executionStats';
import { BrowserExecutionStats } from '../../../model/browserExecutionsStats';
import { ServerVersions } from '../../../model/serverVersions';
import {TranslateService} from '@ngx-translate/core';

const SUCCESS_COLOR = 'alert alert-success';
@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {

  processes: Process[];
  serverVersions: ServerVersions;

  constructor(public dataService: DataService, private translate: TranslateService) { }

  ngOnInit(): void {
    this.getClient();
    this.serverVersions = history.state.serverVersions;
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

}
