import { Component, OnInit} from '@angular/core';
import { Router } from '@angular/router';
import { AutenticacionService } from '../../../services/autenticacion.service';
import { DataService } from '../../../services/data.service';
import { TranslateService} from '@ngx-translate/core';

@Component({
  selector: 'app-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.css']
})
export class MenuComponent implements OnInit {
  flag: string;
  constructor(
    public dataService: DataService,
    public authService: AutenticacionService,
    private router: Router,
    private translate: TranslateService
  ) {
    this.selectFlag(this.translate.getDefaultLang());
  }

  ngOnInit(): void {
  }
  logout(): void {
    this.authService.logout();
    this.router.navigate(['']);
  }

  showNotifications(): void {
    this.dataService.showNotificationsWindow = true;
  }
  useLanguage(language: string): void {
    this.translate.use(language);
    this.selectFlag(language);
  }
  selectFlag(lang: string): void{
    if (lang === 'es'){
      this.flag = 'ES';
    }
    if (lang === 'en'){
      this.flag = 'GB';
    }
  }
}
