import { Component } from '@angular/core';
import {TranslateService} from '@ngx-translate/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {

  constructor(private translate: TranslateService) {
    this.translate.addLangs(['en', 'es']);
    if (this.translate.getLangs().includes(navigator.language)) {
      translate.setDefaultLang(navigator.language);
    } else {
      this.translate.setDefaultLang('en');
    }
  }
}
