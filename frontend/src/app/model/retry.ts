export class Retry {
    retryId: number;
    active: boolean;

    constructor() {
        this.retryId = 0;
        this.active = true;
    }
}