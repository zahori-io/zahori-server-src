import { Process } from "./process";
import { TeamId } from "./teamId";

export class Team {
    id: TeamId;
    admin: boolean;
    name: string;
    processes: Process[] = [];
}