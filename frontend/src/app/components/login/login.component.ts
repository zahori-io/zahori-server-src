import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AutenticacionService } from '../../services/autenticacion.service';
import { ViewEncapsulation } from '@angular/core';
import { DataService } from '../../services/data.service';

@Component({
	selector: 'app-login',
	templateUrl: './login.component.html',
	styleUrls: ['./login.component.css'],
	encapsulation: ViewEncapsulation.None
})
export class LoginComponent implements OnInit {

	loading: boolean = false;
	error: string = "";

	constructor(
		private router: Router,
		private autenticacionService: AutenticacionService,
		private dataService: DataService) { }

	ngOnInit(): void {
		if (this.autenticacionService.getUserLoggedIn()) {
			this.router.navigate(['/app']);
		}
	}

	loginUser(e) {
		e.preventDefault();
		var username = e.target.elements[0].value;
		var password = e.target.elements[1].value;
		this.loading = true;
		this.autenticacionService.login(username, password).then(
			result => {
				if (result === true) {
					// Login OK
					this.router.navigate(['/app/dashboard']);
				} else {
					// Login KO
					this.error = 'El nombre del usuario o la contraseña son incorrectos';
					this.loading = false;
				}
			}
		).catch(e => console.error('error'));

		return false;
	}

}
