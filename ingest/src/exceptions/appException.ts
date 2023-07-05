export class AppException extends Error {
  code: number;
  constructor(code: number) {
    super('An Exception Occurred');
    this.name = 'AppException';
    this.code = code;
  }
}

export enum AppExceptionCode {
  // Common/system
  UNEXPECTED_ERROR = 0,
  INVALID_OBJECT_ID = 1,
  INVALID_OBJECT_IDS = 2,
  NO_SERVER_RESPONSE = 3,

  // WebSocket
  UNSUPPORTED_SOCKET_EVENT = 9000,
  INVALID_SOCKET_EVENT = 9001,
  INVALID_SOCKET_RESPONSE = 9002,
}
