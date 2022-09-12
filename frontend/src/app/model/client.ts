import { Team } from "./team";

export class Client {
    clientId: number;
    active: boolean;
    css: string;
    logo: string;
    name: string;
    numParallel: number;
    clientTeams: Team[];

    constructor() {
      this.clientId = 1;
      this.active = true;
      this.css = '';
      this.logo = '';
      this.name = '';
      this.numParallel = 0;
      this.clientTeams = [];
    }
}
