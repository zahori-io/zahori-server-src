import { Component, OnInit } from '@angular/core';
import { Process } from '../../../model/process';
import { DataService } from '../../../services/data.service';
import { ViewEncapsulation } from '@angular/core';
import { Execution } from '../../../model/excution';
import { CaseExecution } from '../../../model/caseExecution';
import { ExecutionStats } from '../../../model/executionStats';
import { BrowserExecutionStats } from '../../../model/browserExecutionsStats';
import { Observable } from 'rxjs';
import { ServerVersions } from '../../../model/serverVersions';
import { BannerOptions } from '../../utils/banner/banner';
declare var $: any;

const SUCCESS_COLOR: string = "alert alert-success";
@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {

  processes: Process[];
  banner: BannerOptions;
  
  constructor(
    public dataService: DataService
  ) { }

  ngOnInit() {
    this.dataService.getClientFromToken();
    this.banner = new BannerOptions();
    this.verifyNewServerVersionAvailable();
  }

  verifyNewServerVersionAvailable(){
    let serverVersions: ServerVersions = history.state['serverVersions'];
    console.log("latestServerVersion:: " + serverVersions.latestServerVersion);
    console.log("remoteVersion: " + serverVersions.remoteVersion);
    if (serverVersions && serverVersions.remoteVersion != serverVersions.latestServerVersion){
      this.banner = new BannerOptions("Hay disponible una nueva versión del servidor: " + serverVersions.latestServerVersion + " (actual: " + serverVersions.remoteVersion + ")", "", SUCCESS_COLOR, true);
    }
  }

  getProcessLastExecutionStats(process: Process): ExecutionStats {
    var lastExecutionStats: ExecutionStats = new ExecutionStats();
    if (process.executions.length > 0) {
      // TODO validar que la ejecución no este Running
      var lastExecution: Execution = process.executions[0];
      lastExecutionStats.totalPassed = lastExecution.totalPassed;
      lastExecutionStats.totalFailed = lastExecution.totalFailed;

      for (var i = 0; i < lastExecution.casesExecutions.length; i++) {
        var caseExecution: CaseExecution = lastExecution.casesExecutions[i];
        var browserExecutionStats: BrowserExecutionStats = lastExecutionStats.browserStats.get(caseExecution.browser.browserName);
        if (!browserExecutionStats) {
          browserExecutionStats = new BrowserExecutionStats();
          lastExecutionStats.browserStats.set(caseExecution.browser.browserName, browserExecutionStats);
        }

        if ("PASSED" == caseExecution.status) {
          browserExecutionStats.totalPassed += 1;
        }
        if ("FAILED" == caseExecution.status) {
          browserExecutionStats.totalFailed += 1;
        }
        if ("Not executed" == caseExecution.status) {
          browserExecutionStats.totalFailed += 1;
        }
      }

      var percent = 0;
      if (lastExecutionStats.totalPassed + lastExecutionStats.totalFailed > 0) {
        percent = this.getPartsPerUnit(lastExecutionStats);
      }

      $("#contador-" + process.processId).circleProgress({
        value: percent,
        animation: false,
        startAngle: -Math.PI / 2,
        fill: {
          color: '#3ac47d' // green
          //gradient: ['#ff1e41', '#ff5f43']
        }
      });

    } else {
      $("#contador-" + process.processId).circleProgress({
        value: 0,
        animation: false,
        fill: { color: '#3ac47d' }
      });
    }
    return lastExecutionStats;
  }

  getPartsPerUnit(lastExecutionStats: ExecutionStats): number {
    return this.getNumberMaxTwoDecimals(lastExecutionStats.totalPassed / (lastExecutionStats.totalPassed + lastExecutionStats.totalFailed));
  }

  getPercent(lastExecutionStats: ExecutionStats) {
    return this.getPartsPerUnit(lastExecutionStats) * 100;
  }

  getBrowserPartsPerUnit(browserExecutionStats: BrowserExecutionStats): number {
    return this.getNumberMaxTwoDecimals(browserExecutionStats.totalPassed / (browserExecutionStats.totalPassed + browserExecutionStats.totalFailed));
  }

  getBrowserPercent(browserExecutionStats: BrowserExecutionStats) {
    return this.getBrowserPartsPerUnit(browserExecutionStats) * 100;
  }

  getNumberMaxTwoDecimals(percent: number): number {
    // Round parts per unit number (i.e.: 0.50): toFixed(n)  n=2 -> No decimal, n=3 -> one decimal
    return parseFloat(percent.toFixed(2));
  }

  closeBanner() {
    this.banner = new BannerOptions;
  }

}
