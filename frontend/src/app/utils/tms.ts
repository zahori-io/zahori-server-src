import { Injectable } from "@angular/core";
import { Configuration } from "../model/configuration";

@Injectable({
  providedIn: 'root'
})
export class Tms {

  repositoriesThatRequireExecutionId = new Set<string>(["Jira Xray"]);

  constructor() { }

  public isActivated(configuration: Configuration): boolean {
    return configuration.uploadResults;
  } 

  public requiresTestExecutionId(configuration: Configuration): boolean {
    return this.repositoriesThatRequireExecutionId.has(configuration.testRepository.name);
  } 

}
