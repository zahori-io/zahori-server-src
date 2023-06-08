import { Time } from "@angular/common";

export class PeriodicExecution {
    periodicExecutionId: number;
    active: boolean;
    //days: string;
    days: string[] = [];
    time: string;
    uuid: string;
    daysArray: string[];

    setDays(daysArray: string[]) {
        //this.days = daysArray.toString();
        this.days = daysArray;
    }
}