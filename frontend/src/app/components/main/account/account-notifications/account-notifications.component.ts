import { Component, OnInit } from '@angular/core';
import { DataService } from '../../../../services/data.service';
import { BannerOptions } from '../../../../utils/banner/banner';
import { TranslateService } from '@ngx-translate/core';
import { Notification } from 'src/app/model/notification';
import { NotificationEvent } from 'src/app/model/notificationEvent';
import { NotificationMedia } from 'src/app/model/notificationMedia';

const SUCCESS_COLOR = 'alert alert-success';
const ERROR_COLOR = 'alert alert-danger';

@Component({
  selector: 'app-account-notifications',
  templateUrl: './account-notifications.component.html',
  styleUrls: ['./account-notifications.component.css']
})
export class AccountNotificationsComponent implements OnInit {

  banner: BannerOptions;
  notifications: Notification[] = [];
  notificationEvents: NotificationEvent[] = [];
  notificationMedia: NotificationMedia[] = [];

  constructor(
    private dataService: DataService,
    private translate: TranslateService
  ) {
  }

  ngOnInit(): void {
    this.initBanner();
    this.getNotifications();
    this.getNotificationMedia();
    this.getNotificationEvents();
  }

  initBanner() {
    this.banner = new BannerOptions();
  }

  getNotifications() {
    this.dataService.getAccountNotifications().subscribe(
      (notifications) => {
        this.notifications = notifications;
      },
      (error) => {
        this.banner = new BannerOptions('', this.translate.instant('main.account.notifications.getError'), ERROR_COLOR, true);
      }
    );
  }

  getNotificationEvents() {
    this.dataService.getNotificationEvents().subscribe(
      (notificationEvents) => {
        this.notificationEvents = notificationEvents;
      },
      (error) => {
        this.banner = new BannerOptions('', this.translate.instant('main.account.notifications.getEventsError'), ERROR_COLOR, true);
      }
    );
  }

  getNotificationMedia() {
    this.dataService.getNotificationMedia().subscribe(
      (notificationMedia) => {
        this.notificationMedia = notificationMedia;
      },
      (error) => {
        this.banner = new BannerOptions('', this.translate.instant('main.account.notifications.getMediaError'), ERROR_COLOR, true);
      }
    );
  }

  toggleNotification(event: NotificationEvent, media: NotificationMedia) {
    // Verificar si ya existe una notificación para esta combinación de evento y medio
    const notificacionExistente = this.notifications.find(item =>
      item.event.notificationEventId === event.notificationEventId && item.media.notificationMediaId === media.notificationMediaId
    );
  
    if (notificacionExistente) {
      console.log("Notification encontrada");
      // La notificación ya está en la lista, solo actualiza su estado
      notificacionExistente.active = !notificacionExistente.active;
    } else {
      // La notificación no está en la lista, agrégala con el estado activado
      console.log("Notification no encontrada");
      const nuevaNotificacion = {
        notificationId: null,
        event: event,
        media: media,
        active: true
      };
      this.notifications.push(nuevaNotificacion);
    }
  }

  getNotificationStatus(event: NotificationEvent, media: NotificationMedia): boolean {
    const notification = this.notifications.find(notificacion =>
      notificacion.event.notificationEventId === event.notificationEventId && notificacion.media.notificationMediaId === media.notificationMediaId
    );
    return notification ? notification.active : false;
  }

  saveNotifications() {
    this.dataService.saveAccountNotifications(this.notifications).subscribe(
      (notifications) => {
        this.notifications = notifications;
        this.banner = new BannerOptions('', this.translate.instant('main.account.notifications.saveSuccess'), SUCCESS_COLOR, true);
      },
      (error) => {
        this.banner = new BannerOptions('', this.translate.instant('main.account.notifications.saveError'), ERROR_COLOR, true);
      }
    );
  }

  getEventI18n(text: string): string {
    return this.getTextI18n('.event.' + text);
  }

  getMediaI18n(text: string): string {
    return this.getTextI18n('.media.' + text);
  }

  getTextI18n(text: string): string {
    return this.translate.instant('main.account.notifications' + text);
  }
}
