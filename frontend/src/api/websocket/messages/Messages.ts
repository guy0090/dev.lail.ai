import type { Notification } from "@/api/common/Notification";
import { BasicRoleDto, UserSelfDto, UserSettingsDto } from "@/api/common/dto/ResponseDtos";
import { WebSocketEvent } from "@/api/websocket/EventTypes";

/**
 * Base class for all incoming websocket messages (not as responses to commands).
 */
export class EventBody {
  private readonly event: WebSocketEvent;
  private readonly content: any;

  constructor(event: WebSocketEvent, content: any) {
    this.event = event;
    this.content = content;
  }

  getEvent() {
    return this.event;
  }

  getContent() {
    return this.content;
  }

}

export class Welcome extends EventBody {
  private readonly apiVersion?: string = undefined
  private readonly encounters?: number = undefined
  private readonly backendReady: boolean = true

  constructor(content: any) {
    super(WebSocketEvent.Welcome, content);

    if (content.version) this.apiVersion = content.version
    if (content.encounters !== undefined) this.encounters = content.encounters
    if (content.backendReady !== undefined) this.backendReady = content.backendReady

  }

  getApiVersion() {
    return this.apiVersion;
  }

  getEncounters() {
    return this.encounters;
  }

  isBackendReady() {
    return this.backendReady;
  }

}

export class NotificationReceived extends EventBody {
  private readonly notification?: Notification

  constructor(content: any) {
    super(WebSocketEvent.NotificationReceived, content);
    this.notification = undefined // Notification.handleNotification(content.notification)
  }

  getNotification() {
    return this.notification;
  }
}

export class UserDetailsChanged extends EventBody {
  private readonly permissions?: string[]
  private readonly roles?: BasicRoleDto[]
  private readonly settings?: UserSettingsDto
  private readonly user?: UserSelfDto

  constructor(content: any) {
    super(WebSocketEvent.UserDetailsChanged, content);

    if (content.permissions) this.permissions = content.permissions
    if (content.roles) this.roles = content.roles.map((role: any) => new BasicRoleDto(role))
    if (content.settings) this.settings = new UserSettingsDto(content.settings)
    if (content.user) this.user = content.user
  }

  getPermissions() {
    return this.permissions;
  }

  getRoles() {
    return this.roles;
  }

  getSettings() {
    return this.settings;
  }

  getUser() {
    return this.user;
  }
}