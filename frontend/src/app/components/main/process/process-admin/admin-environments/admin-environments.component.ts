import { Component, Input, OnInit } from '@angular/core';
import { Observable, Subscription } from 'rxjs';
import { Environment } from '../../../../../model/environment';
import { DataService } from '../../../../../services/data.service';
import { BannerOptions } from '../../../../utils/banner/banner';
import {TranslateService} from '@ngx-translate/core';

const ERROR = '';
const SUCCESS_COLOR = 'alert alert-success';
const ERROR_COLOR = 'alert alert-danger';

@Component({
  selector: 'app-admin-environments',
  templateUrl: './admin-environments.component.html',
  styleUrls: ['./admin-environments.component.css']
})

export class AdminEnvironmentsComponent implements OnInit {
  envs: Environment[] = [];
  myEnvs: Environment[] = [];
  banner: BannerOptions;

  eventOpenComponentSubscription: Subscription;
  @Input() eventOpenComponent: Observable<void>;
  ngOnInit(): void {
    this.refresh();
    this.eventOpenComponentSubscription = this.eventOpenComponent.subscribe(
      () => {
          console.log('detected component opened');
          this.refresh();
          this.myEnvs = [];
        }
    );
  }

  constructor(public dataService: DataService, private translate: TranslateService) {
  }

  refresh(): void{
    this.dataService.getEnvironments(String(this.dataService.processSelected.processId)).subscribe(
      (res: any) => {
        this.envs = res;
      });

    this.banner = new BannerOptions();
  }

  deleteEnv(env: Environment): void{
    env.active = false;
    const envArray: Environment[] = [env];
    const bannerText = this.translate.instant('main.process.processAdmin.environments.environmentRemoved', {name: env.name});
    this.sendPostPetition(envArray, new BannerOptions('Entorno eliminado: ' + env.name, '', SUCCESS_COLOR , true ));
  }

  updateEnv(env: Environment): void{
    if (env.name.length === 0 || env.url.length === 0){
      // this.banner = new BannerOptions(ERROR, "Todos los campos son obligatorios", ERROR_COLOR , true )
    } else{
      const envArray: Environment[] = [env];
      const bannerText = this.translate.instant('main.process.processAdmin.environments.environmentModified', {name: env.name});
      this.sendPostPetition(envArray, new BannerOptions(bannerText, '', SUCCESS_COLOR , true ));
    }

  }

  createEnv(env: Environment): void{
    if (env.name.length === 0 || env.url.length === 0){
      // this.banner = new BannerOptions(ERROR, "Todos los campos son obligatorios", ERROR_COLOR , true );
    }
    else{
      const envArray: Environment[] = [env];
      const bannerText = this.translate.instant('main.process.processAdmin.environments.environmentCreated', {name: env.name});
      this.sendPostPetition(envArray, new BannerOptions('Entorno creado: ' + env.name, '', SUCCESS_COLOR , true ));
      this.deleteFromArray(env);
    }
  }

  deleteFromArray(env: Environment): void{
    const index: number = this.myEnvs.indexOf(env);
    if (index !== -1){
      this.myEnvs.splice(index, 1);
    }
  }

  sendPostPetition(env: Environment[], banner: BannerOptions): void{
    this.dataService.setEnvironment(env, String(this.dataService.processSelected.processId)).subscribe(
      () => {
        this.refresh();
        this.banner = banner;
      },
    error => {
        console.log('Error en la petición:' + error);
        this.banner = new BannerOptions(ERROR, error, ERROR_COLOR , true );
      });
  }

  closeBanner(): void{
    this.banner = new BannerOptions();
  }

  newEnv(): void{
    console.log('newEnv()');
    this.myEnvs.push(new Environment());
  }
}
