import { Component, OnInit } from '@angular/core';
import { Environment } from '../../../../model/environment';
import { EnvironmentService } from './environmentComponent/environmentService';
import { DataService } from '../../../../services/data.service'
import { AlertOptions } from './alert/alert'


const SUCCESS : string = "Operación realizada con éxito";
const ERROR: string = "Algo ha ido mal";
const SUCCESS_COLOR : string = "alert alert-success";
const ERROR_COLOR : string = "alert alert-danger";

@Component({
  selector: 'app-admin-environments',
  templateUrl: './admin-environments.component.html',
  styleUrls: ['./admin-environments.component.css']
})


export class AdminEnvironmentsComponent implements OnInit {
  envs : Environment[] = [];
  myenvironment : Environment;
  alert: AlertOptions;
  showrow : boolean = false;

  ngOnInit() {
    this.refresh();
    this.myenvironment = new Environment();
    this.alert = new AlertOptions();
  }  
  constructor(public dataService: DataService) {
  }

  refresh(){
    this.dataService.getEnvironments().subscribe(
      (res : any) => {
        this.envs = res.content;
        console.log(res.content);
      });
  }

  deleteEnv(env: Environment){
    this.alert = new AlertOptions(SUCCESS, "El entorno " + env.name + " ha sido eliminado", SUCCESS_COLOR , true )

  }

  updateEnv(env : Environment){
    if (env.name.length == 0 || env.url.length == 0){
      this.alert = new AlertOptions(ERROR, "Todos los campos son obligatorios", ERROR_COLOR , true )
    }
    else{
      this.alert = new AlertOptions(SUCCESS, "Se ha modificado el entorno " + env.name, SUCCESS_COLOR , true )
    }
  }
  
  createEnv(env : Environment){
    if (env.name.length == 0 || env.url.length == 0){
      
      this.alert = new AlertOptions(ERROR, "Todos los campos son obligatorios", ERROR_COLOR , true );
    }
    else{
      this.alert = new AlertOptions(SUCCESS, "Se ha creado el entorno " + env.name, SUCCESS_COLOR , true);
      this.showrow = false;
    }
  }

  closeAlert(){
    this.alert = new AlertOptions;
  }

  newEnv(){
    if (!this.showrow){
      this.myenvironment = new Environment();
    }
      this.showrow = !this.showrow;
  
  }
}
