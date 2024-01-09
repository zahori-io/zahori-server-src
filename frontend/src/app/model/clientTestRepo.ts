import { ClientTestRepoId } from "./clientTestRepoId";
import { TestRepository } from "./test-repository";

export class ClientTestRepo {
    id: ClientTestRepoId;
    user: string;
    password: string;
    url: string;
    active: boolean;
    testRepository: TestRepository;

    constructor() {
        this.id = new ClientTestRepoId();
        this.user = "";
        this.password = "";
        this.url = "";
        this.active = true;
    }
}