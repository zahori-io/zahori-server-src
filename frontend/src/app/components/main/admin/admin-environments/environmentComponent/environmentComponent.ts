import { Component, Input, Output, EventEmitter } from '@angular/core';
import { Environment } from '../../../../../model/environment';
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
    
    submitted : boolean = false;

    testdeleteEnv(env : Environment){
        this.deleted.emit(env);
        console.log("click on borrar");
        this.submitted = true;

    }

    deleteEnv(env : Environment) {
        Swal.fire({
            title: 'Borrar entorno ' + env.name,
            text: 'Esta acción no puede deshacerse',
            icon: 'warning',
            showCancelButton: true,
            confirmButtonText: 'Ok',
            cancelButtonText: 'No',
            backdrop: `
                rgba(64, 69, 58,0.4)
                left top
                no-repeat`
        }).then((result) => {
            if (result.value) {
                Swal.fire({
                    backdrop: `
                        rgba(64, 69, 58,0.4)
                        left top
                        no-repeat`,                    
                    title: 'Eliminado el entorno ' + env.name,
                    text : 'El entorno ha sido eliminado',
                    icon : 'success'
                    }
                )

                this.deleted.emit(env);
                console.log("click on borrar");
                this.submitted = true;
            } else if (result.dismiss === Swal.DismissReason.cancel) {
                Swal.fire({
                    backdrop: `
                        rgba(64, 69, 58,0.4)
                        left top
                        no-repeat`,                    
                    title : 'Acción cancelada',
                    }
                )
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

    isNew(env : Environment){
      return env.order == 0;
  }



}