import { Client } from "./client";
import { TestRepository } from "./test-repository";

export class ClientTestRepo {
    repoInstanceId: number;
    user: string;
    password: string;
    name: string;
    url: string;
    active: boolean;
    client: Client;
    testRepository: TestRepository;

    constructor() {
        this.repoInstanceId = 0;
        this.client = new Client();
        this.testRepository = new TestRepository();
        this.user = "";
        this.password = "";
        this.name = ""
        this.url = "";
        this.active = true;
    }
}