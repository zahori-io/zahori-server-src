import { Component, Input, Output, EventEmitter } from '@angular/core';
import { Configuration } from '../../../../../model/configuration';
import { Environment } from '../../../../../model/environment';
import { EvidenceCase } from '../../../../../model/evidence-case';
import {TestRepository} from '../../../../../model/test-repository';

@Component({
    selector: 'configuratorForm',
    templateUrl: './configuratorForm.component.html'
})
export class ConfiguratorFormComponent{
    @Input()
    configuration : Configuration;
    @Input()
    envs : Environment[];
    @Input()
    evidenceCases : EvidenceCase[];
    @Input()
    testRepositories : TestRepository[];

    typeOfEvidences : String[] =["Video", "Log", "Screenshot", "Doc"]

    saveConf(conf : Configuration){
        console.log(JSON.stringify(conf));
    }

    getTypeOfEvidences(type : string){
        for (var evidence of this.configuration.evidenceTypes){
            if(evidence.name === type){
                return true;
            }
            console.log(evidence.name)
        }
        return false;
    }
}