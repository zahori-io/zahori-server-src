import { Component, OnInit } from '@angular/core';
import { DataService } from '../../../../services/data.service';
import { BannerOptions } from '../../../../utils/banner/banner';
import { TranslateService } from '@ngx-translate/core';

const SUCCESS_COLOR = 'alert alert-success';
const ERROR_COLOR = 'alert alert-danger';

@Component({
  selector: 'app-account-change-password',
  templateUrl: './account-change-password.component.html',
  styleUrls: ['./account-change-password.component.css']
})
export class AccountChangePasswordComponent implements OnInit {

  currentPassword: string;
  newPassword: string;
  confirmPassword: string;
  banner: BannerOptions;

  constructor(
    private dataService: DataService,
    private translate: TranslateService
  ) {
  }

  changePassword() {
    this.dataService.changePassword(this.currentPassword, this.newPassword, this.confirmPassword).subscribe(
      (result) => {
        this.banner = new BannerOptions('', this.translate.instant('main.account.changePassword.ok'), SUCCESS_COLOR, true);
        this.cleanForm();
      },
      (error) => {
        this.banner = new BannerOptions('', error.error, ERROR_COLOR, true);
      }
    );
  }

  ngOnInit(): void {
    this.initBanner();
  }

  cleanForm(){
    this.currentPassword = "";
    this.newPassword = "";
    this.confirmPassword = "";
  }

  initBanner() {
    this.banner = new BannerOptions();
  }

}
