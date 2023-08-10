import { Component, OnInit } from '@angular/core';
import { DataService } from '../../services/data.service';
import { AutenticacionService } from '../../services/autenticacion.service';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-verify-email',
  templateUrl: './verify-email.component.html',
  styleUrls: ['./verify-email.component.css']
})
export class VerifyEmailComponent implements OnInit {

  emailVerified: string = "";
  
  constructor(
    public dataService: DataService,
    private autenticacionService: AutenticacionService,
    public router: Router,
    public activatedRoute: ActivatedRoute
  ) { }

  ngOnInit(): void {
    this.verifyEmail(this.activatedRoute.snapshot.params['token']);
  }

  verifyEmail(token: string){
    if (!token || token.trim() == ""){
      this.emailVerified = "Invalid token";
      return;
    }

    this.dataService.verifyEmail(token).subscribe(
      () => {
        this.emailVerified = 'Email verified!';
        console.log(this.emailVerified);

        if (this.autenticacionService.getUserLoggedIn()) {
          this.router.navigateByUrl('/app/profile/change-email', { state: { message: this.emailVerified, error: ""} });
        } else {
          this.router.navigateByUrl('/login', { state: { message: this.emailVerified, error: ""} });
        }
      },
      (error) => {
        this.emailVerified = 'Error verifying email: ' + error.error;
        console.log(this.emailVerified);

        if (this.autenticacionService.getUserLoggedIn()) {
          this.router.navigateByUrl('/app/profile/change-email', { state: { message: "", error: this.emailVerified} });
        } else {
          this.router.navigateByUrl('/login', { state: { message: "", error: this.emailVerified} });
        }
      }
    );
  }
}
