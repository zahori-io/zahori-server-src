import { BrowserExecutionStats } from "./browserExecutionsStats";

export class ExecutionStats {
    totalPassed: number = 0;
    totalFailed: number = 0;
    browserStats: Map<string, BrowserExecutionStats> = new Map();
}