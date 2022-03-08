import { CaseExecution } from './caseExecution';
import { Configuration } from './configuration';
import { PeriodicExecution } from './periodic-execution';
import { Process } from './process';
import {ProcessSchedule} from './processSchedule';

export class Execution {
    executionId: number;
    date: Date;
    name: string;
    status: string;
    totalFailed: number;
    totalPassed: number;
    durationSeconds: number;
    casesExecutions: CaseExecution[];
    configuration: Configuration;
    process: Process;
    processSchedule: ProcessSchedule;
}
