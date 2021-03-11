import { Team } from "./team";

export class Client {
    clientId: number;
    active: boolean;
    css: string;
    logo: string;
    name: string;
    numParallel: number;
    clientTeams: Team[];
}