import { Component, OnInit } from '@angular/core';
import { DataService } from 'src/app/services/data.service';
import { ForgotPasswordDto } from 'src/app/model/forgotPasswordDto';
import { ActivatedRoute, Router } from '@angular/router';
import { Validator } from 'src/app/utils/validator';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.css']
})
export class ForgotPasswordComponent implements OnInit {

  forgotPasswordDto: ForgotPasswordDto = new ForgotPasswordDto();
  forgotPasswordMessage: string;

  constructor(
    private dataService: DataService,
    public activatedRoute: ActivatedRoute,
    private router: Router,
    private translate: TranslateService
  ) {
  }

  ngOnInit(): void {
    this.forgotPasswordDto.token = this.activatedRoute.snapshot.params['token'];
  }
  
  resetPassword() {
    this.forgotPasswordMessage = null;

    if (!Validator.isValidPassword(this.forgotPasswordDto.newPassword)){
      this.forgotPasswordMessage = this.translate.instant(Validator.invalidPassword);
      return;
    }

    if (this.forgotPasswordDto.newPassword !== this.forgotPasswordDto.confirmPassword){
      this.forgotPasswordMessage = this.translate.instant('home.account.forgot-password.passwordsNotMatch');
      return;
    }

    this.dataService.forgotPasswordReset(this.forgotPasswordDto).subscribe(
      (result) => {
        this.router.navigateByUrl('/login', { state: { message: this.translate.instant('home.account.forgot-password.reset.ok'), error: "" } });
      },
      (error) => {
        this.router.navigateByUrl('/login', { state: { message: "", error: error.error } });
      }
    );
  }

}
