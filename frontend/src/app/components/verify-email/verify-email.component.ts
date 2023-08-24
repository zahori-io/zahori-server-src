import { Component, OnInit } from '@angular/core';
import { DataService } from '../../services/data.service';
import { AutenticacionService } from '../../services/autenticacion.service';
import { ActivatedRoute, Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-verify-email',
  templateUrl: './verify-email.component.html',
  styleUrls: ['./verify-email.component.css']
})
export class VerifyEmailComponent implements OnInit {

  constructor(
    public dataService: DataService,
    private autenticacionService: AutenticacionService,
    public router: Router,
    public activatedRoute: ActivatedRoute,
    private translate: TranslateService
  ) { }

  ngOnInit(): void {
    this.verifyEmail(this.activatedRoute.snapshot.params['token']);
  }

  verifyEmail(token: string) {
    if (!token || token.trim() == "") {
      this.redirectAndDisplay("", this.translate.instant('verify-email.invalidToken'));
      return;
    }

    this.dataService.verifyEmail(token).subscribe(
      () => {
        this.redirectAndDisplay(this.translate.instant('verify-email.emailVerified'), "");
      },
      (error) => {
        let errorMessage = this.translate.instant('verify-email.error');
        if (error.status == 400) {
          errorMessage = errorMessage + this.translate.instant('verify-email.tokenExpired');
        } else {
          errorMessage = errorMessage + error.error;
        }
        console.log(errorMessage);

        this.redirectAndDisplay("", errorMessage);
      }
    );
  }

  redirectAndDisplay(message: string, error: string) {
    if (this.autenticacionService.isUserLoggedIn()) {
      this.router.navigateByUrl('/app/account/change-email', { state: { message: message, error: error } });
    } else {
      this.router.navigateByUrl('/login', { state: { message: message, error: error } });
    }
  }

}
