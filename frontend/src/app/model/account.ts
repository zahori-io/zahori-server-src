import {Client} from './client';
import {Role} from "./role";

export class Account {
  username: string;
  password: string;
  enabled: boolean;
  credentialsexpired: boolean;
  expired: boolean;
  locked: boolean;
  client: Client;
  roles: Role[];
  
  constructor(username: string, password: string) {
    this.username = username;
    this.password = password;
    this.enabled = true;
    this.credentialsexpired = false;
    this.expired = false;
    this.locked = false;
    this.client =  new Client();
    this.roles = [];
    this.roles.push(new Role());
  }
}
