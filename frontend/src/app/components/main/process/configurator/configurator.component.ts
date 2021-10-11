import { Component, OnInit } from '@angular/core';
import { Configuration } from '../../../../model/configuration';
import { DataService } from '../../../../services/data.service';
import { Retry } from '../../../../model/retry';
import { Timeout } from '../../../../model/timeout';
import { EvidenceType } from '../../../../model/evidence-type';
import { BannerOptions } from '../../../utils/banner/banner';
import { Environment } from '../../../../model/environment';
import { EvidenceCase } from '../../../../model/evidence-case';
import { TestRepository } from '../../../../model/test-repository';
import Swal from 'sweetalert2';
import {TranslateService} from '@ngx-translate/core';

const SUCCESS_COLOR = 'alert alert-success';
const ERROR_COLOR = 'alert alert-danger';

@Component({
  selector: 'app-configurator',
  templateUrl: './configurator.component.html',
  styleUrls: ['./configurator.component.css']
})
export class ConfiguratorComponent implements OnInit {

  selectedConfiguration: Configuration = new Configuration();
  envs: Environment[] = [];
  evidenceCases: EvidenceCase[] = [];
  evidenceTypes: EvidenceType[] = [];
  testRepositories: TestRepository[] = [];
  retries: Retry[] = [];
  timeouts: Timeout[] = [];

  banner: BannerOptions;

  constructor(
    public dataService: DataService,
    private translate: TranslateService
  ) { }

  ngOnInit(): void {
    this.getEnvironments();
    this.getCases();
    this.getEvidenceTypes();
    this.getTestRepos();
    this.getRetries();
    this.getTimeouts();
    this.dataService.getProcessConfigurations();

    this.banner = new BannerOptions();
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
    this.banner = new BannerOptions();
  }

  newConfiguration() {
    this.selectedConfiguration = new Configuration();
    this.banner = new BannerOptions();
  }

  removeConfiguration(configuration: Configuration) {
    Swal.fire({
      title: 'Borrar configuración: ' + configuration.name,
      text: 'Esta acción no podrá deshacerse',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Borrar',
      cancelButtonText: 'Cancelar',
      backdrop: `
          rgba(64, 69, 58,0.4)
          left top
          no-repeat`
    }).then((result) => {
      if (result.value) {
        configuration.active = false;
        const configurations: Configuration[] = [configuration];
        this.dataService.saveConfigurations(configurations).subscribe(
          (configurationRemoved) => {
            console.log('Configuration removed');
            this.banner = new BannerOptions('', this.translate.instant('banner.configDeleted'), SUCCESS_COLOR, true);
            this.dataService.getProcessConfigurations();
          },
          (error) => {
            this.banner = new BannerOptions('', this.translate.instant('banner.error') + error.message, ERROR_COLOR, true);
          }
        );
      }
    });
  }

  closeBanner(): void {
    this.banner = new BannerOptions;
  }

}
