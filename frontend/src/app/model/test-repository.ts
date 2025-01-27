import { ClientTestRepo } from "./clientTestRepo";

export class TestRepository {
    testRepoId: number;
    type: string;
    order: number;
    active: boolean;

    constructor() {
        this.testRepoId = 0;
        this.type = "";
        this.order = 0;
        this.active = true;
    }
}