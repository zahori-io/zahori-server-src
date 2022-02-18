import {Process} from './process';
import {Execution} from './excution';

export class ProcessSchedule {
  processScheduleId: number;
  name: string;
  numExecutions: number;
  process: Process;
  executionId: number;
  cronExpression: string;
  nextExecution: Date;
  active: boolean;
  constructor(processScheduleId: number, process: Process, executionId: number, cronExpression: string, nextExecution: Date, name: string, numExecutions: number){
    this.process = process;
    this.executionId = executionId;
    this.cronExpression = cronExpression;
    this.nextExecution = nextExecution;
    this.active = true;
    this.numExecutions = numExecutions;
    this.name = name;
  }
}
