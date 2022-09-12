import { BrowserExecutionStats } from "./browserExecutionsStats";
import { CaseExecution } from "./caseExecution";
import { Execution } from "./execution";
import { ExecutionStats } from "./executionStats";
import { ProcessType } from "./process-type";

export class Process {
  processId: number;
  name: string;
  processType: ProcessType;
  executions: Execution[];
  lastExecutionStats: ExecutionStats = new ExecutionStats();
}