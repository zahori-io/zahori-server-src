import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {AutenticacionService} from '../../services/autenticacion.service';
import {ViewEncapsulation} from '@angular/core';
import {DataService} from '../../services/data.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  loading = false;
  error = false;

  constructor(
    private router: Router,
    private autenticacionService: AutenticacionService,
    private dataService: DataService) {
  }

  ngOnInit(): void {
    if (this.autenticacionService.getUserLoggedIn()) {
      this.router.navigate(['/app']);
    }
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

}
