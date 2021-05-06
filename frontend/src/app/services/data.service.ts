import { Injectable } from '@angular/core';
import { Observable, of, Subject } from 'rxjs';
import { map, catchError } from 'rxjs/operators';

import { HttpClient, HttpHeaders } from '@angular/common/http';

import { AutenticacionService } from './autenticacion.service';
import { Client } from '../model/client';
import { Execution } from '../model/excution';
import { Process } from '../model/process';
import { Team } from '../model/team';
import { Case } from '../model/case';
import { Configuration } from '../model/configuration';
import { Browser } from '../model/browser';
import { Environment } from '../model/environment';
import {ClientTestRepo} from '../model/clientTestRepo';
import {TestRepository} from '../model/test-repository';

@Injectable({
  providedIn: 'root'
})
export class DataService {


  private headers = new HttpHeaders().set('Content-Type', 'application/json');
  private url = '/api/';

  client: Client = new Client();
  teamSelectedInSelector: Team = new Team();
  teamSelected: Team = new Team();
  processSelected: Process = new Process();
  processSelectedChange: Subject<boolean> = new Subject<boolean>();
  processExecutions: Execution[];
  processCases: Case[] = [];
  processConfigurations: Configuration[] = [];
  showNotificationsWindow = false;

  constructor(
    private http: HttpClient,
    private autenticacionService: AutenticacionService
  ) { }

  getClientFromToken(): void {
    this.getClient().subscribe(
      client => {
        this.client = client;
        this.setFirstTeam();
      }
    );
  }

  setFirstTeam() {
    if (this.client.clientTeams && this.client.clientTeams.length > 0) {
      this.teamSelected = this.client.clientTeams[0];
      this.teamSelectedInSelector = this.teamSelected;
    }
  }

  setTeam(team: Team) {
    this.teamSelected = team;
  }

  setProcess(process: Process) {
    this.processSelected = process;
    this.processSelectedChange.next(true);
  }

  getProcessCases() {
    this.getCases().subscribe(
      cases => {
        this.processCases = cases;
      }
    );
  }

  getProcessConfigurations() {
    this.getConfigurations().subscribe(
      configurations => {
        this.processConfigurations = configurations;
      }
    );
  }

  /*
    API CALLS
   */
  private getClient(): Observable<Client> {
    return this.http.get<Client>(this.url + 'client', {});
  }

  public getExecutions(): Observable<Execution[]> {
    return this.http.get<Execution[]>(this.url + "process/" + this.processSelected.processId + "/executions", {});
  }

  private getCases(): Observable<Case[]> {
    return this.http.get<Case[]>(this.url + "process/" + this.processSelected.processId + "/cases", {});
  }

  public getCasesJson(): Observable<string> {
    return this.http.get<string>(this.url + "process/" + this.processSelected.processId + "/cases", { responseType: 'text' as 'json' });
  }

  public saveCases(cases: {}[]): Observable<any> {
    return this.http.post(this.url + "process/" + this.processSelected.processId + "/cases", cases, { responseType: 'text' as 'json' });
  }

  private getConfigurations(): Observable<Configuration[]> {
    return this.http.get<Configuration[]>(this.url + "process/" + this.processSelected.processId + "/configurations", {});
  }

  public getBrowsers(): Observable<Browser[]> {
    return this.http.get<Browser[]>(this.url + "browser", {});
  }

  public getSelenoidUiHostAndPort(): Observable<string> {
    return this.http.get<string>(this.url + "selenoid/ui", { responseType: 'text' as 'json' });
  }

  public createExecution(execution: Execution): Observable<Execution> {
    return this.http.post<Execution>(this.url + "process/" + this.processSelected.processId + "/executions", JSON.stringify(execution), {});
  }

  public getFile(fileUrl: string) {
    return this.http.get(this.url + "process/" + this.processSelected.processId + "/file?path=" + fileUrl, { responseType: 'blob' });
  }

  public getEnvironments(processId: string): Observable<any> {
    return this.http.get<any>(this.url + "process/" + processId + "/environments");
  }

  public setEnvironment(envs: Environment[], processId: string) {
    return this.http.post(this.url + 'process/' + processId + '/environments', JSON.stringify(envs));
  }
  //TMS
  public getClientTestRepo(): Observable<ClientTestRepo[]>{
    return this.http.get<ClientTestRepo[]>(this.url + 'clienttestrepo/', {});
  }

  public setClientTestRepo(clientTestRepo: ClientTestRepo){
    console.log(clientTestRepo);
    return this.http.post(this.url + 'clienttestrepo/upgrade/', JSON.stringify(clientTestRepo));
  }
  public getTestRepository(testId: string): Observable<TestRepository>{
    return this.http.get<TestRepository>(this.url + 'testrepo/' + testId,{});
  }
  public setTestRepository(testRepo: TestRepository): Observable<TestRepository>{

    return this.http.post<TestRepository>(this.url + 'testrepo/upgrade/', JSON.stringify(testRepo));
  }
  public deleteTestRepository(testRepo: TestRepository): Observable<TestRepository>{
    return this.http.post<TestRepository>(this.url + 'testrepo/delete/', JSON.stringify(testRepo));
  }
  // get Jenkins artifact
  //public getFile(fileUrl: string) {
  //  return this.http.get(this.url + "process/" + this.processSelected.processId + "/artifact?url=" + fileUrl, { responseType: 'blob' });
  //}
}
