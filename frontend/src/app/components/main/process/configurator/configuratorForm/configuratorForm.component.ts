import { newArray } from '@angular/compiler/src/util';
import { Component, Input, Output, EventEmitter } from '@angular/core';
import { Configuration } from '../../../../../model/configuration';
import { Environment } from '../../../../../model/environment';
import { EvidenceCase } from '../../../../../model/evidence-case';
import { EvidenceType } from '../../../../../model/evidence-type';
import { Retry } from '../../../../../model/retry';
import { TestRepository } from '../../../../../model/test-repository';
import { Timeout } from '../../../../../model/timeout';
import { DataService } from '../../../../../services/data.service';

@Component({
    selector: 'configuratorForm',
    templateUrl: './configuratorForm.component.html'
})

export class ConfiguratorFormComponent {
    @Input()
    configuration: Configuration;
    @Input()
    envs: Environment[]; 
    @Input()
    evidenceCases: EvidenceCase[];
    @Input()
    evidenceTypes: EvidenceType[];
    @Input()
    testRepositories: TestRepository[];
    @Input()
    retries: Retry[] = [];
    @Input()
    timeouts: Timeout[] = [];

    constructor(
        public dataService: DataService
    ) { }

    saveConf(configuration: Configuration) {
        let configurations: Configuration[] = [configuration];
        this.dataService.saveConfigurations(configurations).subscribe(
            (configurationsSaved) => {
                console.log("Configuration saved");
                this.dataService.getProcessConfigurations();
            },
            (error) => {
                //this.error = error.error;
                //this.loading = false;
            }
        );
    }

    changeEvidenceType(evidenceType: EvidenceType, event: any): void {
        if (event.currentTarget.checked) {
            this.configuration.evidenceTypes.push(evidenceType);    
        } else {
            this.removeEvidenceType(evidenceType);
        }
    }

    removeEvidenceType(evidenceType: EvidenceType) {
        for( var i = 0; i < this.configuration.evidenceTypes.length; i++) { 
            if (this.configuration.evidenceTypes[i].eviTypeId === evidenceType.eviTypeId) {
                this.configuration.evidenceTypes.splice(i, 1); 
            }
        }
    }

    isSelectedEvidenceType(evidenceType: EvidenceType): boolean {
        for (var selectedEvidenceType of this.configuration.evidenceTypes) {
            if (selectedEvidenceType.eviTypeId === evidenceType.eviTypeId) {
                return true;
            }
        }
        return false;
    }

}