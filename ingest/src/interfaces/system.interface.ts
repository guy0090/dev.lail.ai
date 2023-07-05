export interface SystemSettings {
  id: string;
  initDone: boolean;
  uploadsEnabled: boolean;
  showPlayerNames: boolean;
  limits: SystemLimits;
  defaults: SystemDefaults;
}

export interface SystemLimits {
  maxUsers: number;
  maxRoles: number;
  maxEncounters: number;
  maxGuilds: number;
  maxGuildMembers: number;
  maxGuildRoles: number;
}

export interface SystemDefaults {
  defaultGuild?: string;
  defaultRole: string;
  defaultMaxUploads: number;
}
