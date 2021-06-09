import { Component, EventEmitter, Input, OnChanges, OnInit, Output } from '@angular/core';
import { Subject } from 'rxjs';
import { Configuration } from '../../../../../model/configuration';
import { Environment } from '../../../../../model/environment';
import { EvidenceCase } from '../../../../../model/evidence-case';
import { EvidenceType } from '../../../../../model/evidence-type';
import { Retry } from '../../../../../model/retry';
import { TestRepository } from '../../../../../model/test-repository';
import { Timeout } from '../../../../../model/timeout';
import { DataService } from '../../../../../services/data.service';
import { BannerOptions } from '../../../../utils/banner/banner';

const SUCCESS_COLOR: string = "alert alert-success";
const ERROR_COLOR: string = "alert alert-danger";
@Component({
    selector: 'configuratorForm',
    templateUrl: './configuratorForm.component.html'
})

export class ConfiguratorFormComponent implements OnInit, OnChanges {
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

    @Output()
    environmentsChange = new EventEmitter<any>();

    public eventInstantiateEnvironmentCompononent: Subject<void> = new Subject<void>();
    emitEventOpenEnvironmentComponent() {
        this.eventInstantiateEnvironmentCompononent.next();
    }

    banner: BannerOptions;

    constructor(
        public dataService: DataService
    ) { }

    ngOnInit(): void {
        this.banner = new BannerOptions();
    }

    ngOnChanges() {
        this.closeBanner();
    }

    saveConf(configuration: Configuration) {
        let errorMessage = this.validateConfig(configuration);
        if (errorMessage){
            this.banner = new BannerOptions("", errorMessage, ERROR_COLOR, true);
            return;
        }

        if (configuration.testRepositories.length > 0 && configuration.testRepositories[0].testRepoId == 0){
            configuration.testRepositories = [];
        }
        
        let configurations: Configuration[] = [configuration];
        this.dataService.saveConfigurations(configurations).subscribe(
            (configurationsSaved) => {
                console.log("Configuration saved");
                this.dataService.getProcessConfigurations();
                this.banner = new BannerOptions("", "Configuración guardada", SUCCESS_COLOR, true);
            },
            (error) => {
                this.banner = new BannerOptions("", "Error: " + error.message, ERROR_COLOR, true);
            }
        );
    }

    validateConfig(configuration: Configuration): string {
        if (configuration.name == "") {
            return "Escribe el nombre de la configuración";
        }
        if (!configuration.clientEnvironment.environmentId) {
            return "Selecciona un entorno";
        }
        if (!configuration.retry.retryId) {
            return "Selecciona el número de reintentos";
        }
        if (!configuration.evidenceCase.eviCaseId) {
            return "Selecciona cuando se generarán evidencias";
        }
        return null;
    }
    changeEvidenceType(evidenceType: EvidenceType, event: any): void {
        if (event.currentTarget.checked) {
            this.configuration.evidenceTypes.push(evidenceType);
        } else {
            this.removeEvidenceType(evidenceType);
        }
    }

    removeEvidenceType(evidenceType: EvidenceType) {
        for (var i = 0; i < this.configuration.evidenceTypes.length; i++) {
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

    closeBanner() {
        this.banner = new BannerOptions;
    }

    closeEditEnvironmentsModal(){
        this.environmentsChange.next();
    }
}