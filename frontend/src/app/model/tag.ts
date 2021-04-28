export class Tag {
    tagId: number;
    name: string;
    color: string;
    order: number;
    active: boolean;

    constructor() {
        this.tagId = 0;
        this.name = "";
        this.color = "";
        this.order = 0;
        this.active = true;
    }
}