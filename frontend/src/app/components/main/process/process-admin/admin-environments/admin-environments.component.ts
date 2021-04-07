import { Component, OnInit } from '@angular/core';
import { environment } from 'src/environments/environment';
import { Environment } from '../../../../../model/environment';
import { DataService } from '../../../../../services/data.service'
import { BannerOptions } from '../../../../../utils/banner/banner'


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
  banner: BannerOptions;
  showrow : boolean = false;

  ngOnInit() {
    this.refresh();
    this.myenvironment = new Environment();
    this.banner = new BannerOptions();
  }  
  constructor(public dataService: DataService) {
  }

  refresh(){
    this.dataService.getEnvironments(String(this.dataService.processSelected.processId)).subscribe(
      (res : any) => {
        this.envs = res;
      });
  }

  deleteEnv(env: Environment){
    env.active = false;
    let envArray: Environment[] = [env];
    this.sendPostPetition(envArray, new BannerOptions(SUCCESS, "El entorno " + env.name + " ha sido eliminado" + env.name, SUCCESS_COLOR , true ));
  }

  updateEnv(env : Environment){
    if (env.name.length == 0 || env.url.length == 0){
      this.banner = new BannerOptions(ERROR, "Todos los campos son obligatorios", ERROR_COLOR , true )
    }
    else{
      let envArray: Environment[] = [env];
      this.sendPostPetition(envArray, new BannerOptions(SUCCESS, "Se ha modificado el entorno " + env.name, SUCCESS_COLOR , true ));
    }
    
  }
  
  
  createEnv(env : Environment){
    
    if (env.name.length == 0 || env.url.length == 0){
      
      this.banner = new BannerOptions(ERROR, "Todos los campos son obligatorios", ERROR_COLOR , true );
    }
    else{
      let envArray: Environment[] = [env];
      this.sendPostPetition(envArray, new BannerOptions(SUCCESS, "Se ha creado el entorno " + env.name, SUCCESS_COLOR , true ));
    }
  }
  
  sendPostPetition(env : Environment[], banner : BannerOptions){
    this.dataService.setEnvironment(env, String(this.dataService.processSelected.processId)).subscribe(
      (res : any) => {
        this.refresh();
        this.banner = banner
        this.showrow = false;
      });
      error => {
        console.log("Error en la petición:" + error);
        this.banner = new BannerOptions(ERROR, error, ERROR_COLOR , true )
      }
  }

  closeBanner(){
    this.banner = new BannerOptions;
  }
  
  newEnv(){
    if (!this.showrow){
      this.myenvironment = new Environment();
    }
      this.showrow = !this.showrow;
  
  }
}
