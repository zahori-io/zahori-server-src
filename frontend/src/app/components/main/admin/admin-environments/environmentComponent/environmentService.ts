import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { Environment } from '../../../../../model/environment';

const BASE_URL = 'localhost:9090/api/environments';

@Injectable({ providedIn : 'root'})
export class EnvironmentService {

    envs : Environment[] = [];
    constructor(private httpClient: HttpClient) { }

    getData() : Observable<any>{
        return this.httpClient.get(BASE_URL).pipe(
            map((res : any) => res.json()),
			catchError(error => this.handleError(error))
		) 
    }

    private handleError(error: any) {
		console.error(error);
		return throwError("Server error (" + error.status + "): " + error.text())
	}
}