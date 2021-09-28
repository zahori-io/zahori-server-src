import { Component, Input, OnInit } from '@angular/core';
import { Observable, Subscription } from 'rxjs';
import { Environment } from '../../../../../model/environment';
import { DataService } from '../../../../../services/data.service'
import { BannerOptions } from '../../../../utils/banner/banner'

const ERROR: string = "";
const SUCCESS_COLOR : string = "alert alert-success";
const ERROR_COLOR : string = "alert alert-danger";

@Component({
  selector: 'app-admin-environments',
  templateUrl: './admin-environments.component.html',
  styleUrls: ['./admin-environments.component.css']
})

export class AdminEnvironmentsComponent implements OnInit {
  envs : Environment[] = [];
  myEnvs : Environment[] = [];
  banner: BannerOptions;

  eventOpenCompononentSubscription: Subscription;
  @Input() eventOpenCompononent: Observable<void>;

  ngOnInit() {
    this.refresh();
    this.eventOpenCompononentSubscription = this.eventOpenCompononent.subscribe(
      () => {
          console.log("detected component opened");
          this.refresh();
          this.myEnvs = [];
        }
    );
  }  
  
  constructor(public dataService: DataService) {
  }

  refresh(){
    this.dataService.getEnvironments(String(this.dataService.processSelected.processId)).subscribe(
      (res : any) => {
        this.envs = res;
      });

    this.banner = new BannerOptions()
  }

  deleteEnv(env: Environment){
    env.active = false;
    let envArray: Environment[] = [env];
    this.sendPostPetition(envArray, new BannerOptions("Entorno eliminado: " + env.name, "", SUCCESS_COLOR , true ));
  }

  updateEnv(env : Environment){
    if (env.name.length == 0 || env.url.length == 0){
      // this.banner = new BannerOptions(ERROR, "Todos los campos son obligatorios", ERROR_COLOR , true )
    } else{
      let envArray: Environment[] = [env];
      this.sendPostPetition(envArray, new BannerOptions("Entorno modificado: " + env.name, "", SUCCESS_COLOR , true ));
    }
    
  }
  
  createEnv(env : Environment){  
    if (env.name.length == 0 || env.url.length == 0){
      // this.banner = new BannerOptions(ERROR, "Todos los campos son obligatorios", ERROR_COLOR , true );
    }
    else{
      let envArray: Environment[] = [env];
      this.sendPostPetition(envArray, new BannerOptions("Entorno creado: " + env.name, "", SUCCESS_COLOR , true ));
      this.deleteFromArray(env);
    }
  }

  deleteFromArray(env : Environment){
    let index : number = this.myEnvs.indexOf(env);
    if (index !== -1){
      this.myEnvs.splice(index, 1);
    }
  }
  
  sendPostPetition(env : Environment[], banner : BannerOptions){
    this.dataService.setEnvironment(env, String(this.dataService.processSelected.processId)).subscribe(
      (res : any) => {
        this.refresh();
        this.banner = banner
      });
      error => {
        console.log("Error en la petición:" + error);
        this.banner = new BannerOptions(ERROR, error, ERROR_COLOR , true )
      }
  }

  closeBanner(){
    this.banner = new BannerOptions();
  }
  
  newEnv(){
    console.log("newEnv()");
    this.myEnvs.push(new Environment());
  }
}
