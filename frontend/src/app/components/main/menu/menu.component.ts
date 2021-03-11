import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AutenticacionService } from '../../../services/autenticacion.service';
import { ViewEncapsulation } from '@angular/core';
import { DataService } from '../../../services/data.service';

@Component({
  selector: 'app-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class MenuComponent implements OnInit {

  constructor(
    public dataService: DataService,
    public authService: AutenticacionService,
    private router: Router
  ) { }

  ngOnInit(): void {
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['']);
  }

  showNotifications() {
    this.dataService.showNotificationsWindow = true;
  }
}
