export enum UserType {
  SYSTEM,
  USER,
}

export enum PrivacySetting {
  PUBLIC,
  PRIVATE,
  UNLISTED,
}

export interface User {
  id: string;
  discordId: string;
  discordUsername: string;
  discriminator: string;
  avatar: string;
  registeredDate: number;
  lastSeen: number;
  banned: boolean;
  uploads: number;
  maxUploads: number;
}

export interface UserSettings {
  _id: string;
  username?: string;
  profilePrivacySetting: PrivacySetting;
  uploadPrivacySetting: PrivacySetting;
  customUrlSlug?: string;
  profileColor?: string;
  region?: string;
  server?: string;
}

export interface UserDetails {
  user: User;
  settings: UserSettings;
  permissions: string[];
}
