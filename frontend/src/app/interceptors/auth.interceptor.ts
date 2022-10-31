import { Injectable } from '@angular/core';
import { HttpErrorResponse, HttpEvent, HttpHandler, HttpHeaders, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Observable, of, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AutenticacionService } from '../services/autenticacion.service';
import { Router } from '@angular/router';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

	constructor(public autenticacionService: AutenticacionService, private router: Router) {
	}

	intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
		const token = localStorage.getItem('currentUser');
		if (!token) {
			return next.handle(req);
		}

		const authHeaders: HttpHeaders = this.autenticacionService.crearCabeceraJwt();
		const authReq = req.clone({
			headers: authHeaders
		});
		return next.handle(authReq).pipe(catchError((error: HttpErrorResponse)=> this.handleAuthError(error)));
	}
		
    private handleAuthError(error: HttpErrorResponse): Observable<any> {
        if (error.status === 401 || error.status === 403) {
			console.log("Http error " + error + " -> redirect to login");
			this.autenticacionService.logout();
			this.router.navigate(['./login']);
            // if you've caught / handled the error, you don't want to rethrow it unless you also want downstream consumers to have to handle it as well.
            return of(error.message); // or EMPTY may be appropriate here
        }
        return throwError(error);
    }
}