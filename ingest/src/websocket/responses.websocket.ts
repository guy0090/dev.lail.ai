import { SystemSettingsDto, UserDetailsDto } from '@/dtos/user.dto';
import { AppException, AppExceptionCode } from '@/exceptions/appException';

export class DeferredResponse<T extends WebSocketResponseData = any> {
  deferral: Promise<WebSocketResponse<T>>;

  resolve!: (value: WebSocketResponse<T>) => void;
  reject!: (reason?: any) => void;

  created: number;

  constructor() {
    this.created = Date.now();
    this.deferral = new Promise<WebSocketResponse<T>>((resolve, reject) => {
      this.resolve = resolve;
      this.reject = reject;
    });
  }
}

export class WebSocketResponse<Data extends WebSocketResponseData> {
  data?: Data;
  error?: ResponseError;
  assoc: string;
  created: number;

  constructor(event: any) {
    this.data = event.data;
    this.error = event.error ? new ResponseError(event.error) : undefined;
    this.assoc = event.assoc;
    this.created = event.created;

    if (!this.assoc || (!this.data && !this.error)) throw new AppException(AppExceptionCode.INVALID_SOCKET_RESPONSE);
  }

  toJSON() {
    return {
      data: this.data,
      error: this.error,
      assoc: this.assoc,
      created: this.created,
    };
  }
}

export class ResponseError {
  code?: number;
  message: string;

  constructor(error?: any) {
    this.code = error?.code || -1;
    this.message = error?.message || 'Unknown error';
  }
}

//#region Response Data
/**
 * Base class for response data.
 */
export abstract class WebSocketResponseData {
  message: string;
  protected abstract result?: any;
  constructor(data: any) {
    this.message = data.message;
  }
}

export class PublishPendingEncounterData extends WebSocketResponseData {
  result: any;
  constructor(data: any) {
    super(data);
    this.result = data.result;
  }
}

export class GetUserDetailsData extends WebSocketResponseData {
  result: UserDetailsDto;
  constructor(data: any) {
    super(data);
    this.result = new UserDetailsDto(data.result);
  }
}

export class GetSystemSettingsData extends WebSocketResponseData {
  result: SystemSettingsDto;
  constructor(data: any) {
    super(data);
    this.result = new SystemSettingsDto(data.result);
  }
}
//#endregion
