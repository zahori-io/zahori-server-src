import { Component, Input, Output, EventEmitter } from '@angular/core';
import { Environment } from '../../../../../model/environment';

@Component({
    selector: 'environment',
    templateUrl: './environmentComponent.html'
})

export class EnvironmentComponent{
    @Input()
    environment : Environment;

    @Output()
    deleted = new EventEmitter<Environment>();

    @Output()
    updated = new EventEmitter<Environment>();

    @Output()
    created = new EventEmitter<Environment>();

    errorFound : boolean = false;

    deleteEnv(env : Environment){
        this.deleted.emit(env);
        console.log("click on borrar");
    }

    updateEnv(env : Environment){
        this.updated.emit(env);
        console.log("click on update");
    }

    createEnv(env : Environment){
        this.created.emit(env);
        console.log("click on create");
    }

    isNew(env : Environment){
      return env.order == 0;
  }

  errorNotif(){
      this.errorFound = true;
  }
}