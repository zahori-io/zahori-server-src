import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AutenticacionService } from '../../../services/autenticacion.service';
import { DataService } from '../../../services/data.service';
import { TranslateService } from '@ngx-translate/core';
import { I18nService } from 'src/app/services/i18n.service';

@Component({
  selector: 'app-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.css']
})
export class MenuComponent implements OnInit {

  constructor(
    public dataService: DataService,
    public authenticationService: AutenticacionService,
    public i18nService: I18nService,
    private router: Router,
    private translate: TranslateService
  ) {
  }

  ngOnInit(): void {
  }

  logout(): void {
    this.authenticationService.logout();
    this.router.navigate(['']);
  }

  showNotifications(): void {
    this.dataService.showNotificationsWindow = true;
  }
}
