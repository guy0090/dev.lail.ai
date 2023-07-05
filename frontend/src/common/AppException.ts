export class AppException extends Error {
  code: number
  constructor(code: number) {
    super("An Exception Occurred")
    this.name = 'AppException'
    this.code = code
  }
}

export enum AppExceptionCode {
  // Common/system
  UnexpectedError = 0,
  InvalidObjectId = 1,
  InvalidObjectIds = 2,
  NoServerResponse = 3,

  // Profiles/users
  InvalidUserId = 1000,
  InvalidSlug = 1001,

  // WebSocket
  UnsupportedSocketEvent = 9000,
  InvalidSocketEvent = 9001,
  InvalidSocketResponse = 9002,

  // Notifications
  InvalidNotificationType = 10000,
}