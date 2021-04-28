import { Component, Input, Output, EventEmitter } from '@angular/core';
import { Environment } from '../../../../../../model/environment';
import Swal from 'sweetalert2/dist/sweetalert2.js';

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
    @Output()
    erased = new EventEmitter<Environment>();

    submitted : boolean = false;

    deleteEnv(env : Environment) {
        Swal.fire({
            title: 'Borrar entorno: ' + env.name,
            text: 'Esta acción no puede deshacerse',
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
                this.deleted.emit(env);
                console.log("click on borrar");
                this.submitted = true;
            } 

        })
    }

    updateEnv(env : Environment){
        this.updated.emit(env);
        console.log("click on update");
        this.submitted = true;
    }

    createEnv(env : Environment){
        this.created.emit(env);
        console.log("click on create");
        this.submitted = true;
    }

    deleteFromArray(env : Environment){
        this.erased.emit(env);
    }

    isNew(env : Environment){
      return env.environmentId == 0;
  }



}