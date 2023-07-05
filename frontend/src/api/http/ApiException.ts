import type { AxiosError } from "axios"

export class ApiException extends Error {
  status: number
  message: string
  path: string
  timestamp: string
  id?: string
  code?: number

  constructor(exception: Exception) {
    super(exception.message)
    this.name = 'ApiException'
    this.status = exception.status
    this.message = exception.message
    this.path = exception.path
    this.timestamp = exception.timestamp
    this.id = exception.id
    this.code = exception.code
  }

  static fromCode(id: number, message?: string) {
    return new ApiException({
      status: 500,
      message: message || "An exception occurred",
      path: "",
      timestamp: "",
      code: id
    })
  }

  static fromAxios(error: AxiosError<Exception>) {
    return new ApiException(error.response?.data || {
      status: error.response?.status || 500,
      message: error.message,
      path: "",
      timestamp: new Date().toISOString()
    })
  }
}

export interface Exception {
  status: number
  message: string
  path: string
  timestamp: string
  id?: string
  code?: number
}

export enum ExceptionCode {
  // Common/system
  COMMON = 0,
  SAVE_DATA = 1,
  INVALID_OBJECT_ID = 2,
  VALIDATION_FAILED = 3,
  MISSING_AUTH_PRINCIPAL = 4,
  SYSTEM_SETTINGS_NOT_FOUND = 5,
  SIGN_UPS_DISABLED = 6,

  // Users
  USER_NOT_FOUND = 1000,
  USER_SETTINGS_COLLISION = 1001,
  USER_SETTINGS_NOT_FOUND = 1002,
  USER_ALREADY_IN_GUILD = 1003,
  // Roles
  ROLES_DELETE_SUPER_USER_ROLE = 2000,
  ROLES_DUPLICATE = 2001,
  // Permissions
  PERMISSIONS_NOT_FOUND = 3000,
  // Guilds
  GUILD_NOT_FOUND = 4000,
  GUILD_ROLE_NOT_FOUND = 4001,
  GUILD_MEMBER_NOT_FOUND = 4002,
  GUILD_MEMBER_ALREADY_IN_GUILD = 4003,
  GUILD_DELETE_PERMANENT_ROLE = 4004,
  GUILD_INVALID_PERMISSIONS = 4005,
  GUILD_ROLE_REMOVE = 4006,
  GUILD_REMOVE_OWNER = 4007,
  // API Keys
  KEY_NOT_FOUND = 5000,
  KEY_NO_PERMISSIONS = 5001,
  // Discord
  DISCORD_GET_GRANT = 6000,
  DISCORD_GET_USER = 6001,
  DISCORD_REFRESH_GRANT = 6002,
  // Followers
  // Encounters
  ENCOUNTER_NOT_FOUND = 8000,
  ENCOUNTER_UPLOAD_LIMIT = 8001,
  // Sockets
  INVALID_WS_CONTENT = 9000,
  DUPLICATE_ASSOCIATION_ID = 9001,
  MAX_PENDING_COMMANDS = 9002,
  INVALID_WS_COMMAND = 9003,
  // Notifications
  NOTIFICATION_NOT_FOUND = 10000,
  NOTIFICATION_UPDATE_FAILED = 10001
}
