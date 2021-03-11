import { BrowserExecutionStats } from "./browserExecutionsStats";
import { CaseExecution } from "./caseExecution";
import { Execution } from "./excution";
import { ExecutionStats } from "./executionStats";
import { ProcessType } from "./process-type";

export class Process {
  processId: number;
  name: string;
  processType: ProcessType;
  executions: Execution[];

  getLastExecutionStats(): ExecutionStats {
    var lastExecutionStats: ExecutionStats = new ExecutionStats();
    if (this.executions.length > 0) {
      // TODO validar que la ejecución no este Running
      var lastExecution: Execution = this.executions[this.executions.length - 1];
      lastExecutionStats.totalPassed = lastExecution.totalPassed;
      lastExecutionStats.totalFailed = lastExecution.totalFailed;

      for (var i = 0; i < lastExecution.casesExecutions.length; i++) {
        var caseExecution: CaseExecution = lastExecution.casesExecutions[i];
        var browserExecutionStats: BrowserExecutionStats = lastExecutionStats.browserStats.get(caseExecution.browser.browserName);
        if (!browserExecutionStats) {
          browserExecutionStats = new BrowserExecutionStats();
          lastExecutionStats.browserStats.set(caseExecution.browser.browserName, browserExecutionStats);
        }

        if ("Passed" == caseExecution.status) {
          browserExecutionStats.totalPassed += 1;
        }
        if ("Failed" == caseExecution.status) {
          browserExecutionStats.totalFailed += 1;
        }
      }
    }

    return lastExecutionStats;
  }
}