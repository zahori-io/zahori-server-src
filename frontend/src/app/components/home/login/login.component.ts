import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Location } from '@angular/common';
import { AutenticacionService } from '../../../services/autenticacion.service';
import { DataService } from 'src/app/services/data.service';
import { I18nService } from 'src/app/services/i18n.service';
import { Validator } from 'src/app/utils/validator';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  loading = false;
  loginError = false;

  forgotPasswordEmail: string;
  showForgotPassword = false;
  forgotPasswordMessage: string;

  constructor(
    private router: Router,
    private location: Location,
    private autenticacionService: AutenticacionService,
    private dataService: DataService,
    private i18nService: I18nService,
    private translate: TranslateService
    ) {
  }

  ngOnInit(): void {
    if (this.autenticacionService.isUserLoggedIn()) {
      this.router.navigate(['/app']);
    }
  }

  loginUser(e): boolean {
    e.preventDefault();
    this.loginError = false;
    const username = e.target.elements[0].value;
    const password = e.target.elements[1].value;
    this.loading = true;
    this.autenticacionService.login(username, password).then(
      result => {
        if (result === true) {  // Login OK

          // Get user account details
          this.dataService.getAccount().subscribe(
            account => {
              this.autenticacionService.account = account;

              // Set language
              if (account.language){
                this.i18nService.setCurrentLanguage(account.language);
              } else {
                this.i18nService.saveUserLanguage(this.i18nService.currentLanguage);
              }
            }
          );

          this.router.navigate(['/app/dashboard']);
        } else { // Login KO
          this.loginError = true;
          this.loading = false;
        }
      }
    ).catch(err => console.error('error: ' + err.message()));

    return false;
  }

  forgotPasswordRequest(){
    this.forgotPasswordMessage = null;

    if (!Validator.isValidEmail(this.forgotPasswordEmail)){
      this.forgotPasswordMessage = this.translate.instant(Validator.invalidEmail);
      return;
    }

    this.dataService.getEmailServiceStatus().subscribe(
      // email service is configured
      () => {

        this.dataService.forgotPasswordRequest(this.forgotPasswordEmail).subscribe(
          () => {
            this.forgotPasswordMessage = this.translate.instant('home.login.forgot-password.ok');
            this.forgotPasswordEmail = null;
          },
          (error) => {
            this.forgotPasswordMessage = "Error";
          }
        );

      },
      // email service is not configured
      (error) => {
        this.forgotPasswordMessage = this.translate.instant('home.login.forgot-password.emailServiceNotConfigured');
      }
    );
  }

}
