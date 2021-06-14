import { ClientTestRepoId } from "./clientTestRepoId";

export class ClientTestRepo {
    id: ClientTestRepoId;
    user: string;
    password: string;
    url: string;
    active: boolean;

    constructor() {
        this.id = new ClientTestRepoId();
        this.user = "";
        this.password = "";
        this.url = "";
        this.active = true;
    }
}