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
import {Account} from '../model/account';
import { Configuration } from '../model/configuration';
import { Browser } from '../model/browser';
import { Environment } from '../model/environment';
import { Tag } from '../model/tag';
import { EvidenceCase } from '../model/evidence-case'; import { ClientTestRepo } from '../model/clientTestRepo';
import { TestRepository } from '../model/test-repository';
import { Retry } from '../model/retry';
import { Timeout } from '../model/timeout';
import { EvidenceType } from '../model/evidence-type';
import { ServerVersions } from '../model/serverVersions';
import {Resolution} from '../model/resolution';

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
        this.processCases.forEach(pc => {
          pc.selected = false;
        });
      }
    );
  }

  getProcessConfigurations() {
    this.getConfigurations().subscribe(
      configurations => {
        this.processConfigurations = configurations;
        // console.log(JSON.stringify(this.processConfigurations))
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
    return this.http.get<Execution[]>(this.url + 'process/' + this.processSelected.processId + '/executions', {});
  }

  public getCases(): Observable<Case[]> {
    return this.http.get<Case[]>(this.url + 'process/' + this.processSelected.processId + '/cases', {});
  }

  public getEviCases(): Observable<EvidenceCase[]> {
    return this.http.get<EvidenceCase[]>(this.url + 'evidenceCase', {});
  }

  public getEvidenceTypes(): Observable<EvidenceType[]> {
    return this.http.get<EvidenceType[]>(this.url + 'evidenceType', {});
  }

  public getCasesJson(): Observable<string> {
    return this.http.get<string>(this.url + 'process/' + this.processSelected.processId + '/cases', { responseType: 'text' as 'json' });
  }

  public saveCases(cases: {}[]): Observable<any> {
    return this.http.post(this.url + 'process/' + this.processSelected.processId + '/cases', cases, { responseType: 'text' as 'json' });
  }

  private getConfigurations(): Observable<Configuration[]> {
    return this.http.get<Configuration[]>(this.url + 'process/' + this.processSelected.processId + '/configurations', {});
  }

  public saveConfigurations(configurations: Configuration[]): Observable<Configuration[]> {
    return this.http.post<Configuration[]>(this.url + 'process/' + this.processSelected.processId + '/configurations', JSON.stringify(configurations));
  }

  public getBrowsers(): Observable<Browser[]> {
    return this.http.get<Browser[]>(this.url + 'browser', {});
  }

  public getSelenoidUiHostAndPort(): Observable<string> {
    return this.http.get<string>(this.url + 'selenoid/ui', { responseType: 'text' as 'json' });
  }

  public createExecution(execution: Execution): Observable<Execution> {
    return this.http.post<Execution>(this.url + 'process/' + this.processSelected.processId + '/executions', JSON.stringify(execution), {});
  }

  public getFile(fileUrl: string) {
    return this.http.get(this.url + 'process/' + this.processSelected.processId + '/file?path=' + fileUrl, { responseType: 'blob' });
  }

  public getEnvironments(processId: string): Observable<any> {
    return this.http.get<any>(this.url + 'process/' + processId + '/environments');
  }

  public setEnvironment(envs: Environment[], processId: string) {
    return this.http.post(this.url + 'process/' + processId + '/environments', JSON.stringify(envs));
  }

  public getTags(processId: String) {
    return this.http.get(this.url + 'process/' + processId + '/tags');
  }

  public setTags(tags: Tag[], processId: string) {
    return this.http.post(this.url + 'process/' + processId + '/tags', JSON.stringify(tags));
  }

  public getTestRepositories() {
    return this.http.get(this.url + 'repositories');
  }

  public getRetries(): Observable<Retry[]> {
    return this.http.get<Retry[]>(this.url + 'retries');
  }

  public getTimeouts(): Observable<Timeout[]> {
    return this.http.get<Timeout[]>(this.url + 'timeouts');
  }

  // TMS
  public getClientTestRepo(): Observable<ClientTestRepo[]> {
    return this.http.get<ClientTestRepo[]>(this.url + 'clienttestrepo/', {});
  }

  public setClientTestRepo(clientTestRepo: ClientTestRepo): Observable<ClientTestRepo> {
    return this.http.post<ClientTestRepo>(this.url + 'clienttestrepo/upgrade/', JSON.stringify(clientTestRepo));
  }

  public getTestRepository(testId: string): Observable<TestRepository> {
    return this.http.get<TestRepository>(this.url + 'testrepo/' + testId, {});
  }

  public setTestRepository(testRepo: TestRepository): Observable<TestRepository> {
    return this.http.post<TestRepository>(this.url + 'testrepo/upgrade/', JSON.stringify(testRepo));
  }

  public deleteTestRepository(testRepo: TestRepository): Observable<TestRepository> {
    return this.http.post<TestRepository>(this.url + 'testrepo/delete/', JSON.stringify(testRepo));
  }

  public getServerVersions(): Observable<ServerVersions> {
    return this.http.get<ServerVersions>(this.url + 'version', {});
  }

  public setSignUpUser(account: Account): Observable<Account> {
    return this.http.post<Account>('/users/sign-up', JSON.stringify(account), { headers: this.headers });
  }
  public getResolutions(processId: string): Observable<any> {
    return this.http.get<any>(this.url + 'resolutions/' + processId);
  }

  public setResolutions(res: Resolution[], processId: string): Observable<object> {
    return this.http.post(this.url + 'resolutions/' + processId, JSON.stringify(res));
  }
  // get Jenkins artifact
  // public getFile(fileUrl: string) {
  //  return this.http.get(this.url + "process/" + this.processSelected.processId + "/artifact?url=" + fileUrl, { responseType: 'blob' });
  // }
}
