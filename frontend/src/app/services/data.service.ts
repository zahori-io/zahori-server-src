import { Injectable } from '@angular/core';
import { Observable, of, Subject } from 'rxjs';
import { map, catchError } from 'rxjs/operators';

import { HttpClient, HttpHeaders } from '@angular/common/http';

import { Client } from '../model/client';
import { Execution } from '../model/execution';
import { Page } from '../model/page';
import { ApiResponse } from '../model/apiResponse';
import { Process } from '../model/process';
import { Team } from '../model/team';
import { Case } from '../model/case';
import { Account } from '../model/account';
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
import { Resolution } from '../model/resolution';
import { Router } from '@angular/router';
import { PeriodicExecution } from '../model/periodic-execution';
import { EmailDto } from '../model/emailDto';
import { Language } from '../model/language';
import { ForgotPasswordDto } from '../model/forgotPasswordDto';
import { CaseExecution } from '../model/caseExecution';
import { Notification } from 'src/app/model/notification';
import { NotificationEvent } from '../model/notificationEvent';
import { NotificationMedia } from '../model/notificationMedia';

@Injectable({
  providedIn: 'root'
})
export class DataService {


  private headers = new HttpHeaders().set('Content-Type', 'application/json');
  public url = './api/';

  client: Client = new Client();
  teamSelectedInSelector: Team = new Team();
  teamSelected: Team = new Team();
  processSelected: Process = new Process();
  processSelectedChange: Subject<boolean> = new Subject<boolean>();
  processExecutions: ApiResponse<Page<Execution>>;
  processCases: Case[] = [];
  processConfigurations: Configuration[] = [];
  showNotificationsWindow = false;

  constructor(
    private http: HttpClient,
    private router: Router
  ) {
    this.initializeData();
  }

  initializeData(){
    this.client = new Client();
    this.teamSelectedInSelector = new Team();
    this.teamSelected = new Team();
    this.processSelected = new Process();
    this.processSelectedChange = new Subject<boolean>();
    this.processExecutions = null;
    this.processCases = [];
    this.processConfigurations = [];
    this.showNotificationsWindow = false;
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

  isWebProcess(): boolean {
    return this.processSelected.processType.name === 'BROWSER';
  }

  isDashboardPage() {
    return this.router.url === "/app/dashboard";
  }

  /*
    API CALLS
   */
  
  public getAccount(): Observable<Account> {
    return this.http.get<Account>(this.url + 'account');
  }

  public getClient(): Observable<Client> {
    return this.http.get<Client>(this.url + 'client', {});
  }

  public getExecutions(): Observable<Execution[]> {
    return this.http.get<Execution[]>(this.url + 'process/' + this.processSelected.processId + '/executions', {});
  }

  public getExecutionsPageable(page: number, size: number): Observable<ApiResponse<Page<Execution>>> {
    return this.http.get<ApiResponse<Page<Execution>>>(this.url + 'process/' + this.processSelected.processId + '/executions/pageable?page=' + page + '&size=' + size, {});
  }

  public getLastExecution(processId: number): Observable<ApiResponse<Page<Execution>>> {
    return this.http.get<ApiResponse<Page<Execution>>>(this.url + 'process/' + processId + '/executions/pageable?page=0&size=1', {});
  }

  public getCaseExecutions(caseId: number): Observable<CaseExecution[]> {
    return this.http.get<CaseExecution[]>(this.url + 'process/' + this.processSelected.processId + '/executions/' + caseId, {});
  }

  public getPeriodicExecutions(): Observable<Execution[]> {
    return this.http.get<Execution[]>(this.url + 'process/' + this.processSelected.processId + '/periodic-executions', {});
  }

  public savePeriodicExecutions(executions: Execution[]): Observable<Execution[]> {
    return this.http.post<Execution[]>(this.url + 'process/' + this.processSelected.processId + '/periodic-executions', JSON.stringify(executions));
  }

  public deletePeriodicExecution(execution: Execution): Observable<any> {
    return this.http.delete<any>(this.url + 'process/' + this.processSelected.processId + '/periodic-executions/' + execution.executionId, {});
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

  public getEnvironments(processId: number): Observable<any> {
    return this.http.get<any>(this.url + 'process/' + processId + '/environments');
  }

  public setEnvironment(envs: Environment[], processId: number) {
    return this.http.post(this.url + 'process/' + processId + '/environments', JSON.stringify(envs));
  }

  public getTags(processId: number) {
    return this.http.get(this.url + 'process/' + processId + '/tags');
  }

  public setTags(tags: Tag[], processId: number) {
    return this.http.post(this.url + 'process/' + processId + '/tags', JSON.stringify(tags));
  }

  public getRetries(): Observable<Retry[]> {
    return this.http.get<Retry[]>(this.url + 'retries');
  }

  public getTimeouts(): Observable<Timeout[]> {
    return this.http.get<Timeout[]>(this.url + 'timeouts');
  }

  // TMS
  public getClientTestRepositories(): Observable<ClientTestRepo[]> {
    return this.http.get<ClientTestRepo[]>(this.url + 'clientTestRepos', {});
  }

  public saveClientTestRepository(clientTestRepo: ClientTestRepo): Observable<ClientTestRepo> {
    return this.http.post<ClientTestRepo>(this.url + 'clientTestRepos', JSON.stringify(clientTestRepo));
  }

  public deleteClientTestRepository(repoInstanceId: number): Observable<any> {
    return this.http.delete<any>(this.url + 'clientTestRepos/' + repoInstanceId, {});
  }
  
  public getTestRepositories(): Observable<TestRepository[]> {
    return this.http.get<TestRepository[]>(this.url + 'testRepositories', {});
  }

  public getServerVersions(): Observable<ServerVersions> {
    return this.http.get<ServerVersions>(this.url + 'version', {});
  }

  public getResolutions(processId: number): Observable<Resolution[]> {
    return this.http.get<any>(this.url + 'resolutions/' + processId);
  }

  public setResolutions(res: Resolution[], processId: number): Observable<object> {
    return this.http.post(this.url + 'resolutions/' + processId, JSON.stringify(res));
  }

  public setSignUpUser(account: Account): Observable<Account> {
    return this.http.post<Account>('./account/sign-up', JSON.stringify(account), { headers: this.headers });
  }

  public verifyEmail(token: string): Observable<string> {
    return this.http.get('./account/verify-email/' + token, { responseType: 'text' });
  }

  public forgotPasswordRequest(email: string): Observable<string> {
    return this.http.post('./account/forgot-password/request', email, { responseType: 'text' });
  }
  
  public forgotPasswordReset(forgotPasswordDto: ForgotPasswordDto): Observable<object> {
    return this.http.post('./account/forgot-password/reset', JSON.stringify(forgotPasswordDto), { headers: this.headers });
  }

  public getEmail(): Observable<EmailDto> {
    return this.http.get<EmailDto>(this.url + 'account/email');
  }

  public updateEmail(newEmail: string): Observable<string> {
    return this.http.post(this.url + 'account/email', newEmail, { responseType: 'text' });
  }

  public changePassword(currentPassword: string, newPassword: string, confirmPassword: string): Observable<string> {
    return this.http.post(this.url + 'account/password',
      JSON.stringify({
        currentPassword: currentPassword,
        newPassword: newPassword,
        confirmPassword: confirmPassword
      }),
      { responseType: 'text' }
    );
  }

  public getEmailServiceStatus(): Observable<any> {
    return this.http.get('./email-service/status');
  }

  public getAvailableLanguages(): Observable<Language[]> {
    return this.http.get<Language[]>('./languages');
  }

  public saveUserLanguage(language: Language): Observable<any> {
    return this.http.post<any>(this.url + 'account/language', JSON.stringify(language));
  }

  public getAccountNotifications(): Observable<Notification[]> {
    return this.http.get<Notification[]>(this.url + 'notifications');
  }

  public saveAccountNotifications(notifications: Notification[]): Observable<any> {
    return this.http.post<any>(this.url + 'notifications', JSON.stringify(notifications));
  }

  public getNotificationEvents(): Observable<NotificationEvent[]> {
    return this.http.get<NotificationEvent[]>(this.url + 'notifications/events');
  }

  public getNotificationMedia(): Observable<NotificationMedia[]> {
    return this.http.get<NotificationMedia[]>(this.url + 'notifications/media');
  }

}
