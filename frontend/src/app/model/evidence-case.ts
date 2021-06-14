export class EvidenceCase{
    eviCaseId: number;
    active: boolean;
    name: string;
    order: number;
    
    constructor() {
        this.eviCaseId = 0;
        this.active = true;
        this.name = "";
        this.order = 0;
    }
}