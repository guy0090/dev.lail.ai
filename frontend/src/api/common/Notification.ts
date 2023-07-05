import { AppException, AppExceptionCode } from "@/common/AppException";

export enum NotificationType {}

export abstract class Notification<T extends NotificationContent = any> {
  type: NotificationType;
  timestamp: number;
  id: string;
  seen: boolean;
  content: T;

  constructor(notification: any) {
    this.type = notification.type;
    this.timestamp = notification.timestamp;
    this.id = notification.id;
    this.seen = notification.seen;
    this.content = notification.content;
  }

  static handleNotification(notification: Notification) {
    switch (notification.type) {
      default:
        throw new AppException(AppExceptionCode.InvalidNotificationType)
    }
  }
}

export interface NotificationContent {}