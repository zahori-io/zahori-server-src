export class Timeout {
    timeoutId: number;
    active: boolean;

    constructor() {
        this.timeoutId = null;
        this.active = true;
    }
}