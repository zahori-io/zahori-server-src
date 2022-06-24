import {Component, EventEmitter, Input, Output} from '@angular/core';
import {Resolution} from '../../../../../../model/resolution';
import Swal from 'sweetalert2';
import {TranslateService} from '@ngx-translate/core';

@Component({
  selector: 'app-resolution',
  templateUrl: './resolution-component.component.html',
  styleUrls: ['./resolution-component.component.css']
})
export class ResolutionComponentComponent {
  @Input()
  resolution: Resolution;
  @Output()
  deleted = new EventEmitter<Resolution>();
  @Output()
  updated = new EventEmitter<Resolution>();
  @Output()
  created = new EventEmitter<Resolution>();
  @Output()
  erased = new EventEmitter<Resolution>();

  submitted = false;

  constructor(private translate: TranslateService) {
  }


  createEnv(res: Resolution): void{
    this.created.emit(res);
    this.submitted = true;
  }

  updateEnv(res: Resolution): void{
    this.updated.emit(res);
    this.submitted = true;
  }

  deleteEnv(res: Resolution): void {
    const bannerText = this.translate.instant('main.process.processAdmin.resolutions.resolution.removeMessage', {width: res.width, height: res.height});
    Swal.fire({
      title: bannerText,
      text: this.translate.instant('main.process.processAdmin.resolutions.resolution.removeWarning'),
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: this.translate.instant('main.process.processAdmin.resolutions.resolution.delete'),
      cancelButtonText: this.translate.instant('main.process.processAdmin.resolutions.resolution.cancel'),
      backdrop: `
                rgba(64, 69, 58,0.4)
                left top
                no-repeat`
    }).then((result) => {
      if (result.value) {
        this.deleted.emit(res);
        this.submitted = true;
      }

    });
  }

  deleteFromArray(res: Resolution): void{
    this.erased.emit(res);
  }

  isNew(res: Resolution): boolean {
    return res.resolutionId === 0;
  }
}
