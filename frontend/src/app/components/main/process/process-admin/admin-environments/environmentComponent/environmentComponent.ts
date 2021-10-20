import {Component, Input, Output, EventEmitter} from '@angular/core';
import {Environment} from '../../../../../../model/environment';
import Swal from 'sweetalert2/dist/sweetalert2.js';
import {TranslateService} from '@ngx-translate/core';

@Component({
  selector: 'environment',
  templateUrl: './environmentComponent.html',
  styleUrls: ['./environmentComponent.css']
})

export class EnvironmentComponent {
  @Input()
  environment: Environment;

  @Output()
  deleted = new EventEmitter<Environment>();

  @Output()
  updated = new EventEmitter<Environment>();

  @Output()
  created = new EventEmitter<Environment>();
  @Output()
  erased = new EventEmitter<Environment>();

  submitted = false;

  constructor(private translate: TranslateService) {
  }

  createEnv(env: Environment): void {
    this.created.emit(env);
    this.submitted = true;
  }

  updateEnv(env: Environment): void  {
    this.updated.emit(env);
    this.submitted = true;
  }

  deleteEnv(env: Environment): void  {
    const bannerText = this.translate.instant('main.process.processAdmin.environments.environment.removeMessage', {name: env.name});
    Swal.fire({
      title: bannerText,
      text: this.translate.instant('main.process.processAdmin.environments.environment.removeWarning'),
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: this.translate.instant('main.process.processAdmin.environments.environment.dalete'),
      cancelButtonText: this.translate.instant('main.process.processAdmin.environments.environment.cancel'),
      backdrop: `
                rgba(64, 69, 58,0.4)
                left top
                no-repeat`
    }).then((result) => {
      if (result.value) {
        this.deleted.emit(env);
        this.submitted = true;
      }

    });
  }

  deleteFromArray(env: Environment): void  {
    this.erased.emit(env);
  }

  isNew(env: Environment): boolean  {
    return env.environmentId === 0;
  }


}
