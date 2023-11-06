import { NotificationEvent } from "./notificationEvent";
import { NotificationMedia } from "./notificationMedia";

export class Notification {
    notificationId: number;
    event: NotificationEvent;
    media: NotificationMedia;
    active: boolean;
}