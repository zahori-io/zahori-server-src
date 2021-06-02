import { Component, OnInit } from '@angular/core';
import { Environment } from 'src/app/model/environment';
import { EvidenceCase } from 'src/app/model/evidence-case';
import { TestRepository } from 'src/app/model/test-repository';
import { Configuration } from '../../../../model/configuration';
import { DataService } from '../../../../services/data.service';
import { stringify } from '@angular/compiler/src/util';
import { Retry } from '../../../../model/retry';
import { Timeout } from '../../../../model/timeout';
import { EvidenceType } from '../../../../model/evidence-type';

declare var $: any;

@Component({
  selector: 'app-configurator',
  templateUrl: './configurator.component.html',
  styleUrls: ['./configurator.component.css']
})
export class ConfiguratorComponent implements OnInit {

  selectedConfiguration: Configuration = new Configuration();
  envs: Environment[] = [];
  evidenceCases: EvidenceCase[] = []
  evidenceTypes: EvidenceType[] = []
  testRepositories: TestRepository[] = []
  retries: Retry[] = [];
  timeouts: Timeout[] = [];
  hide: boolean = true;

  constructor(
    public dataService: DataService
  ) { }

  ngOnInit(): void {
    this.getEnvironments();
    this.getCases();
    this.getEvidenceTypes();
    this.getTestRepos();
    this.getRetries();
    this.getTimeouts();
    this.dataService.getProcessConfigurations();
  }

  getEnvironments() {
    this.dataService.getEnvironments(String(this.dataService.processSelected.processId)).subscribe(
      (res: any) => {
        this.envs = res;
      });
  }

  getTestRepos() {
    this.dataService.getTestRepositories().subscribe(
      (res: any) => {
        this.testRepositories = res;
      });
  }

  getRetries() {
    this.dataService.getRetries().subscribe(
      (response: Retry[]) => {
        this.retries = response;
      });
  }

  getTimeouts() {
    this.dataService.getTimeouts().subscribe(
      (response: Timeout[]) => {
        this.timeouts = response;
      });
  }

  getCases() {
    this.dataService.getEviCases().subscribe(
      (res: any) => {
        this.evidenceCases = res;
      });
  }

  getEvidenceTypes() {
    this.dataService.getEvidenceTypes().subscribe(
      (evidenceTypes: EvidenceType[]) => {
        this.evidenceTypes = evidenceTypes;
      });
  }

  editConfiguration(configuration: Configuration) {
    this.selectedConfiguration = configuration;
    this.hide = true;
  }

  newConfiguration() {
    this.selectedConfiguration = new Configuration();
    this.hide = true;
  }

  removeConfiguration(configuration: Configuration) {
    configuration.active = false;

    let configurations: Configuration[] = [configuration];
    this.dataService.saveConfigurations(configurations).subscribe(
      (configurationRemoved) => {
        console.log("Configuration removed");
        this.dataService.getProcessConfigurations();
      },
      (error) => {
        //this.error = error.error;
        //this.loading = false;
      }
    );
  }

}
