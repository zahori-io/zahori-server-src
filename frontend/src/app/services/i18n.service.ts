import { Injectable } from '@angular/core';
import { DataService } from './data.service';
import { Language } from '../model/language';
import { TranslateService } from '@ngx-translate/core';

@Injectable({
  providedIn: 'root'
})
export class I18nService {

  languages: Language[] = [];
  currentLanguage: Language;
  languageLocalStorage = "language";

  constructor(
    private translate: TranslateService,
    private dataService: DataService
  ) {
  }

  setLanguage() {
    this.translate.setDefaultLang("en");
    const browserLang = this.getBrowserLanguage();
    const languageLocalStorage: Language = this.getStorageLanguage();
    if (languageLocalStorage?.languageCode) {
      this.setCurrentLanguage(languageLocalStorage);
    } else {
      this.translate.use(browserLang);
    }

    this.dataService.getAvailableLanguages().subscribe(
      (languagesInDB: Language[]) => {
        this.languages = languagesInDB;

        console.log("Languages available: " + JSON.stringify(this.languages));
        
        for (let language of this.languages) {
          if (!languageLocalStorage && language.languageCode == browserLang) {
            this.setCurrentLanguage(language);
          }
          if (language.defaultLanguage) {
            this.translate.setDefaultLang(language.languageCode);
          }
        }
      },
      (error) => {
        console.log("error getting languages: " + error.error);

        const languageLocalStorage: Language = this.getStorageLanguage();
        if (languageLocalStorage) {
          this.setCurrentLanguage(languageLocalStorage);
        }
      },
      () => {
        this.printDefaultLanguage();
        this.printBrowserLanguage();
        this.printStorageLanguage();
        this.printCurrentLanguage();
      }

    );
  }

  setCurrentLanguage(language: Language): void {
    this.currentLanguage = language;
    this.translate.use(language.languageCode);
    this.printCurrentLanguage();

    this.setStorageLanguage(language);
  }

  saveUserLanguage(language: Language): void {
    this.dataService.saveUserLanguage(language).subscribe(
      () => {
        console.log("Account language updated");
      },
      (error) => {
        console.log("Error updating account language: " + error.error);
      }
    );
  }

  getBrowserLanguage(): string {
    const browserLang = this.translate.getBrowserLang(); // es
    // this.browserLang = this.translate.getBrowserCultureLang(); // "es-ES"
    // this.browserLang = 'it';
    return browserLang;
  }

  getStorageLanguage(): Language {
    return JSON.parse(localStorage.getItem(this.languageLocalStorage));
  }

  getCurrentLanguage(): string {
    return this.translate.currentLang;
  }

  setStorageLanguage(language: Language) {
    localStorage.setItem(this.languageLocalStorage, JSON.stringify(language));
  }

  printDefaultLanguage() {
    console.log("Default language: " + this.translate.defaultLang);
  }

  printBrowserLanguage() {
    console.log("Browser language: " + this.getBrowserLanguage());
  }

  printCurrentLanguage() {
    console.log("Current language: " + this.getCurrentLanguage());
  }

  printStorageLanguage() {
    console.log("Storage language: " + this.getStorageLanguage()?.languageCode);
  }

}
