import { Link } from './link';

export class Environment {
    //environmentId: number;
    active: boolean;
    name: string;
    order: number;
    url: string;
    links : Link[];
    content : any[];

    constructor() {
     //   this.environmentId = 0;
        this.active = true;
        this.name = "";
        this.order = 0;
        this.url = "";
        this.links = [];
        this.content = [];
    }
}