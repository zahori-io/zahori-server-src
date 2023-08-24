import { Injectable } from '@angular/core';
import { HttpHeaders, HttpClient } from '@angular/common/http';
import { Account } from '../model/account';

@Injectable()
export class AutenticacionService {

  public token: string;
  public account: Account;
  private userLoggedIn: boolean;
  private headers = new HttpHeaders().set(
    'Content-Type', 'application/json');
  private loginUrl = './login';

  constructor(private http: HttpClient) {
    this.userLoggedIn = false;
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
      this.userLoggedIn = true;
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
            this.userLoggedIn = true;

            // almacenar el nombre de usuario y el token jwt en el 
            // local storage para mantener al usuario logado 
            // entre refrescos de página
            localStorage.setItem('currentUser',
              JSON.stringify(
                { username: username, token: token }
              )
            );

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
    this.userLoggedIn = false;
  }

  isUserLoggedIn() {
    return this.userLoggedIn;
  }

  getUsername(): string {
    if (this.account && this.account.username.length > 0) {
      return this.account.username;
    }
    if (this.account && this.account.email.length > 0) {
      return this.account.email;
    }
    return "";
  }

}