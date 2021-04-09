import { Component, OnInit } from '@angular/core';
import { Environment } from 'src/app/model/environment';
import { Configuration } from '../../../../model/configuration';
import { DataService } from '../../../../services/data.service';

declare var $: any;

@Component({
  selector: 'app-configurator',
  templateUrl: './configurator.component.html',
  styleUrls: ['./configurator.component.css']
})
export class ConfiguratorComponent implements OnInit {

  selectedConfiguration: Configuration = new Configuration();
  envs : Environment[] = [];

  constructor(
    public dataService: DataService
  ) { }

  ngOnInit(): void {
    this.dataService.getProcessConfigurations();
    this.getEnvironments();
  }

  getEnvironments(){
    this.dataService.getEnvironments(String(this.dataService.processSelected.processId)).subscribe(
      (res : any) => {
        this.envs = res;
      });
  }

  selectConfiguration(configuration: Configuration) {
    this.selectedConfiguration = configuration;
  }

  newConfiguration() {
    this.selectedConfiguration = new Configuration();
  }

  saveConfiguration(configuration: Configuration) {
    // TODO: send to server

    this.selectedConfiguration = new Configuration();
  }
}
