import { Injectable } from '@angular/core';
import { HttpHeaders, HttpClient } from '@angular/common/http';

@Injectable()
export class AutenticacionService {


  public token: string;
  private isUserLoggedIn: boolean;
  private username: string;
  private headers = new HttpHeaders().set(
    'Content-Type', 'application/json');
  private loginUrl = './login';

  constructor(private http: HttpClient) {
    this.isUserLoggedIn = false;
    // Establecer el token si se ha guardado en el localStorage
    var currentUser;
    try {
      currentUser = JSON.parse(
        localStorage.getItem('currentUser'));
    } catch (e) {
      // Valor invalido
    }

    this.token = currentUser && currentUser.token;

    if (currentUser) {
      this.isUserLoggedIn = true;
    }
  }

  login(username: string, password: string): Promise<boolean> {
    return this.http.post<any>(this.loginUrl,
      JSON.stringify({
        username: username,
        password: password
      }),
      { headers: this.headers })
      .toPromise().then(
        response => {
          let token = response && response.token;
          if (token) {
            // establecer la propiedad token
            this.token = token;
            this.isUserLoggedIn = true;
            // almacenar el nombre de usuario y el token jwt en el 
            // local storage para mantener al usuario logado 
            // entre refrescos de página
            localStorage.setItem('currentUser',
              JSON.stringify(
                { username: username, token: token }));

            // retornar true para indicar un login exitoso
            return true;
          } else {
            // retornar false para indicar que falló el login
            return false;
          }
        }
      ).catch(err => { return false; });
  }

  logout(): void {
    // Remove token and clear the local storage
    this.token = null;
    localStorage.clear();
    this.isUserLoggedIn = false;
  }

  getUserLoggedIn() {
    return this.isUserLoggedIn;
  }

  crearCabeceraJwt(): HttpHeaders {
    let headers = new HttpHeaders({
      'Authorization': this.token,
      'Content-Type': 'application/json'
    });
    return headers;
  }

  getUsername(): string {
    if (this.isUserLoggedIn && localStorage.getItem('currentUser')) {
      return JSON.parse(localStorage.getItem('currentUser')).username;
    } else {
      return "";
    }
  }
}