import { SystemDefaults, SystemLimits, SystemSettings } from '@/interfaces/system.interface';
import { User, UserDetails, UserSettings } from '@/interfaces/users.interface';

export class UserDetailsDto {
  user: User;
  settings: UserSettings;
  permissions: string[];

  constructor(details: UserDetails) {
    this.user = details.user;
    this.settings = details.settings;
    this.permissions = details.permissions;
  }
}

export class SystemSettingsDto {
  id: string;
  initDone: boolean;
  uploadsEnabled: boolean;
  showPlayerNames: boolean;
  limits: SystemLimits;
  defaults: SystemDefaults;

  constructor(settings: SystemSettings) {
    this.id = settings.id;
    this.initDone = settings.initDone;
    this.uploadsEnabled = settings.uploadsEnabled;
    this.showPlayerNames = settings.showPlayerNames;
    this.limits = settings.limits;
    this.defaults = settings.defaults;
  }
}
