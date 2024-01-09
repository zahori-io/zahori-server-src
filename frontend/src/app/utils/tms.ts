import { Injectable } from "@angular/core";
import { Configuration } from "../model/configuration";

@Injectable({
  providedIn: 'root'
})
export class Tms {

  public static readonly XRAY_SERVER: string = "Xray Server";
  public static readonly XRAY_CLOUD: string = "Xray Cloud";
  public static readonly TEST_LINK: string = "TestLink";
  public static readonly HP_ALM: string = "Hp Alm";
  public static readonly AZURE_TEST_PLANS: string = "Azure Test Plans";

  repositoriesThatRequireExecutionId = new Set<string>([Tms.XRAY_SERVER, Tms.XRAY_CLOUD]);

  constructor() { }

  public isActivated(configuration: Configuration): boolean {
    return configuration.uploadResults;
  } 

  public requiresTestExecutionId(configuration: Configuration): boolean {
    return this.repositoriesThatRequireExecutionId.has(configuration.testRepository.name);
  } 

}
