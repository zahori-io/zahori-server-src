import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { AutenticacionService } from '../services/autenticacion.service';

@Injectable()
export class AuthGuard implements CanActivate {

  constructor(private autenticacionService: AutenticacionService,
    private router: Router) { }

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): boolean {

    if (!this.autenticacionService.isUserLoggedIn()) {
      this.router.navigate(['./login']);
    }

    return this.autenticacionService.isUserLoggedIn();
  }
}