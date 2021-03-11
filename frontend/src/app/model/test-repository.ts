import { ClientTestRepo } from "./clientTestRepo";

export class TestRepository {
    testRepoId: number;
    name: string;
    order: number;
    active: boolean;
    clientTestRepos: ClientTestRepo[];

    constructor() {
        this.testRepoId = 0;
        this.name = "";
        this.order = 0;
        this.active = true;
        this.clientTestRepos = [];
    }
}