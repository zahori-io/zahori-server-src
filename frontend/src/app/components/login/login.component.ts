import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {Location} from '@angular/common';
import {AutenticacionService} from '../../services/autenticacion.service';
import { BannerOptions } from '../utils/banner/banner';

const SUCCESS_COLOR = 'alert alert-success';
const ERROR_COLOR = 'alert alert-danger';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  banner: BannerOptions;
  loading = false;
  error = false;

  constructor(
    private router: Router,
    private location: Location,
    private autenticacionService: AutenticacionService,) {
  }

  ngOnInit(): void {
    if (this.autenticacionService.getUserLoggedIn()) {
      this.router.navigate(['/app']);
    }

    this.initBanner();
    this.displayState();
  }

  loginUser(e): boolean {
    e.preventDefault();
    const username = e.target.elements[0].value;
    const password = e.target.elements[1].value;
    this.loading = true;
    this.autenticacionService.login(username, password).then(
      result => {
        if (result === true) {
          // Login OK
          this.router.navigate(['/app/dashboard']);
        } else {
          // Login KO
          this.error = true;
          this.loading = false;
        }
      }
    ).catch(err => console.error('error: ' +  err.message()));

    return false;
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

  initBanner() {
    this.banner = new BannerOptions();
  }
}
