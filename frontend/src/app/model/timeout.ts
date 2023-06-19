export class Timeout {
    timeoutId: number|null;
    active: boolean;

    constructor() {
        this.timeoutId = null;
        this.active = true;
    }
}