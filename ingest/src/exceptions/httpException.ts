import { randomUUID } from 'crypto';

export enum HttpExceptionCode {
  INTERNAL_SERVER_ERROR = 0,
  BAD_REQUEST = 1,
  UNSUPPORTED_UPLOAD = 2,
  INVALID_PAYLOAD = 3,
  TOO_OLD = 4,
  UPLOAD_QUOTA_EXCEEDED = 5,
  UPLOADING_DISABLED = 6,
  SYSTEM_ENCOUNTER_LIMIT_EXCEEDED = 7,
  INVALID_TOKEN = 8,
  TOO_MANY_CONCURRENT_UPLOADS = 9,
}

export class HttpException extends Error {
  public status: number;
  public message: string;
  public id: string;
  public code: HttpExceptionCode;

  constructor(status: number, message: string, code?: HttpExceptionCode) {
    super(message);
    this.status = status;
    this.message = message;
    this.id = randomUUID();
    this.code = code ?? HttpExceptionCode.BAD_REQUEST;
  }
}
