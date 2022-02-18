import {Component, Input, OnInit} from '@angular/core';
import {BannerOptions} from '../../../../utils/banner/banner';
import {Resolution} from '../../../../../model/resolution';
import {DataService} from '../../../../../services/data.service';
import {Observable, Subscription} from 'rxjs';
import {TranslateService} from '@ngx-translate/core';

const ERROR = '';
const SUCCESS_COLOR = 'alert alert-success';
const ERROR_COLOR = 'alert alert-danger';

@Component({
  selector: 'app-admin-resolutions',
  templateUrl: './admin-resolutions.component.html',
  styleUrls: ['./admin-resolutions.component.css']
})
export class AdminResolutionsComponent implements OnInit {
  banner: BannerOptions;
  resolutions: Resolution[];
  newResolutions: Resolution[];

  constructor(public dataService: DataService, private translate: TranslateService) {
    this.refresh();
    this.newResolutions = [];
  }

  ngOnInit(): void {
  }

  refresh(): void {
    this.dataService.getResolutions(String(this.dataService.processSelected.processId)).subscribe(
      (res: any) => {
        this.resolutions = res;
      });
    this.banner = new BannerOptions();
  }

  deleteRes(res: Resolution): void {
    res.active = false;
    const resArray: Resolution[] = [res];
    const bannerText = this.translate.instant('main.process.processAdmin.resolutions.resolutionRemoved', {width: res.width, height: res.height});
    this.saveResolution(resArray, new BannerOptions(bannerText, '', SUCCESS_COLOR, true));
  }

  updateRes(res: Resolution): void {
    if (res.width === 0 || res.height === 0) {
      const bannerText = this.translate.instant('main.process.processAdmin.resolutions.allFieldsObligatory');
      this.banner = new BannerOptions(ERROR, bannerText, ERROR_COLOR, true);
    } else {
      const resArray: Resolution[] = [res];
      const bannerText = this.translate.instant('main.process.processAdmin.resolutions.resolutionModified', {width: res.width, height: res.height});
      this.saveResolution(resArray, new BannerOptions(bannerText, '', SUCCESS_COLOR, true));
    }

  }

  createRes(res: Resolution): void {
    if (res.width === 0 || res.height === 0) {
      const bannerText = this.translate.instant('main.process.processAdmin.resolutions.invalidResolution');
      this.banner = new BannerOptions(ERROR, bannerText, ERROR_COLOR, true);
    } else {
      const resArray: Resolution[] = [res];
      const bannerText = this.translate.instant('main.process.processAdmin.resolutions.resolutionCreated', {width: res.width, height: res.height});
      this.saveResolution(resArray, new BannerOptions(bannerText, '', SUCCESS_COLOR, true));
      this.deleteFromArray(res);
    }
  }

  deleteFromArray(res: Resolution): void {
    const index: number = this.newResolutions.indexOf(res);
    if (index !== -1) {
      this.newResolutions.splice(index, 1);
    }
  }

  saveResolution(res: Resolution[], banner: BannerOptions): void {
    this.dataService.setResolutions(res, String(this.dataService.processSelected.processId)).subscribe(
      () => {
        this.refresh();
        this.banner = banner;
      },
      (error) => {
        console.log('Error en la petición:' + error);
        this.banner = new BannerOptions(ERROR, error, ERROR_COLOR, true);
      });
  }

  newRes(): void {
    this.newResolutions.push(new Resolution());
  }

  closeBanner(): void {
    this.banner = new BannerOptions();
  }
}
