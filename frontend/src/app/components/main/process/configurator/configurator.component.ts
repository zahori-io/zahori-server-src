import { Component, OnInit } from '@angular/core';
import { Environment } from 'src/app/model/environment';
import { EvidenceCase } from 'src/app/model/evidence-case';
import {TestRepository} from 'src/app/model/test-repository';
import { Configuration } from '../../../../model/configuration';
import { DataService } from '../../../../services/data.service';
import { stringify } from '@angular/compiler/src/util';

declare var $: any;

@Component({
  selector: 'app-configurator',
  templateUrl: './configurator.component.html',
  styleUrls: ['./configurator.component.css']
})
export class ConfiguratorComponent implements OnInit {

  selectedConfiguration: Configuration = new Configuration();
  envs : Environment[] = [];
  evidenceCases : EvidenceCase[] = []
  testRepositories : TestRepository[] = []
  hide : boolean = true;

  constructor(
    public dataService: DataService
  ) { }

  ngOnInit(): void {
    this.dataService.getProcessConfigurations();
    this.getEnvironments();
    this.getCases();
    this.getTestRepos();
  }

  getEnvironments(){
    this.dataService.getEnvironments(String(this.dataService.processSelected.processId)).subscribe(
      (res : any) => {
        this.envs = res;
      });
  }

  getTestRepos(){
    this.dataService.getTestRepositories().subscribe(
      (res : any) => {
        this.testRepositories = res;
      });
  }

  getCases(){
    this.dataService.getEviCases().subscribe(
      (res : any) => {
        this.evidenceCases = res;
      });
  }

  selectConfiguration(configuration: Configuration) {
    this.selectedConfiguration = configuration;
    this.hide = true;
  }

  newConfiguration() {
    this.selectedConfiguration = new Configuration();
    this.hide = true;
  }

  saveConfiguration(configuration: Configuration) {
    // TODO: send to server

    this.selectedConfiguration = new Configuration();
  }


}
