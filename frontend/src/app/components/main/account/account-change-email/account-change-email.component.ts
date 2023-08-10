import { Component, OnInit } from '@angular/core';
import { DataService } from '../../../../services/data.service';
import { BannerOptions } from '../../../utils/banner/banner';
import { EmailDto } from '../../../../model/emailDto';
import { Location } from '@angular/common';

const SUCCESS_COLOR = 'alert alert-success';
const ERROR_COLOR = 'alert alert-danger';

@Component({
  selector: 'app-account-change-email',
  templateUrl: './account-change-email.component.html',
  styleUrls: ['./account-change-email.component.css']
})
export class AccountChangeEmailComponent implements OnInit {

  banner: BannerOptions;
  currentEmail: string;
  emailDto: EmailDto;
  validEmail: boolean = true;
  message: string = "";
  error: string = "";
  processing: boolean = false;

  constructor(
    private dataService: DataService,
    private location: Location
  ) {
  }

  ngOnInit(): void {
    this.initBanner();
    this.displayState();
    this.getCurrentEmails();
  }

  displayState(){
    let state:any = this.location.getState();

    if (state.message && state.message != ""){
      this.banner = new BannerOptions('', state.message, SUCCESS_COLOR, true);
    }
    if (state.error && state.error != ""){
      this.banner = new BannerOptions('', state.error, ERROR_COLOR, true);
    }
  }

  getCurrentEmails() {
    this.dataService.getEmail().subscribe(
      (emailDto) => {
        this.emailDto = emailDto;
        this.currentEmail = emailDto.email;
        // if user has no email but has a pending request
        if (emailDto.email === "" && emailDto.newEmail !== ""){
          this.emailDto.email = emailDto.newEmail;
        } 
        this.isValidEmail(this.emailDto.email);
      },
      (error) => {
        this.banner = new BannerOptions('', 'Error getting email', ERROR_COLOR, true);
        console.error('Error getting email: ' + error.error);
      }
    );
  }
  
  isValidEmail(email: string) {
    if (/^[a-zA-Z0-9]+([\._-]?[a-zA-Z0-9]+)*@[a-zA-Z0-9]+([\.-]?[a-zA-Z0-9]+)*(\.[a-zA-Z0-9]{2,})+$/.test(email)) {
      this.validEmail = true;
    } else {
      this.validEmail = false;
    }
  }

  isEmailVerified(){
    return this.currentEmail && this.currentEmail.trim() != "" && this.currentEmail == this.emailDto.email.trim();
  }

  updateEmail() {
    let newEmail = this.emailDto.email.trim().toLowerCase();
    this.processing = true;
    this.dataService.updateEmail(newEmail).subscribe(
      () => {
        this.banner = new BannerOptions('', 'Email change requested, verify your email', SUCCESS_COLOR, true);
        this.getCurrentEmails();
        this.processing = false;
      },
      (error) => {
        let errorMessage = error.error;
        if (error.status == 503){
          errorMessage = "Email service is not enabled, contact your system administrator.";
        }
        this.banner = new BannerOptions('', 'Error updating email: ' + errorMessage, ERROR_COLOR, true);
        this.processing = false;
      }
    );
  }

  initBanner() {
    this.banner = new BannerOptions();
  }
}
