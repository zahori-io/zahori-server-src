import { Injectable } from '@angular/core';
import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Language } from '../model/language';

@Injectable()
export class I18nInterceptor implements HttpInterceptor {

	constructor() {
	}

	intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
		
		const language: Language = this.getStorageLanguage();
		if (language) {
			let headers = request.headers;
			headers.set('Accept-Language', language.languageCode);
		
			request = request.clone({
				headers: headers
			});
		}

		return next.handle(request);
	}

	/* Note/ToDo:
		i18nService can't be injected and used due to:  "Error: NG0200: Circular dependency in DI detected..."
		--> you cannot use a service within your interceptor itself using HttpClientbecause the HttpClient has a dependency on the interceptor. A workaround is to provide a brand new instance of HttpClient free of any inteceptors.
		I18nInterceptor -> i18nService -> dataService -> HttpClient
	*/
	getStorageLanguage(): Language {
		return JSON.parse(localStorage.getItem("language"));
	}

}