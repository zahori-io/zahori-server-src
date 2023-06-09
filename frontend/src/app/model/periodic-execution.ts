import { Time } from "@angular/common";

export class PeriodicExecution {
    periodicExecutionId: number;
    active: boolean;
    days: string[] = [];
    time: string;
    uuid: string;
}