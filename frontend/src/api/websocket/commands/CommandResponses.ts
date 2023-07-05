import { Notification } from '@/api/common/Notification'
import { UserDto } from '@/api/common/dto/ResponseDtos'
import { AppException, AppExceptionCode } from '@/common/AppException'

export class DeferredResponse<T extends WebSocketResponseData = any> {
  deferral: Promise<WebSocketResponse<T>>

  resolve!: (value: WebSocketResponse<T>) => void
  reject!: (reason?: any) => void

  created: number

  constructor() {
    this.created = Date.now()
    this.deferral = new Promise<WebSocketResponse<T>>((resolve, reject) => {
      this.resolve = resolve
      this.reject = reject
    })
  }
}


export class WebSocketResponse<Data extends WebSocketResponseData> {
  data?: Data
  error?: ResponseError
  assoc: string
  created: number

  constructor(event: any) {
    this.data = event.data
    this.error = event.error ? new ResponseError(event.error) : undefined
    this.assoc = event.assoc
    this.created = event.created

    if (!this.assoc || !this.data && !this.error) throw new AppException(AppExceptionCode.InvalidSocketResponse)
  }


  toJSON() {
    return {
      data: this.data,
      error: this.error,
      assoc: this.assoc,
      created: this.created,
    }
  }
}


export class ResponseError {
  code?: number
  message: string

  constructor(error?: any) {
    this.code = error?.code || -1
    this.message = error?.message || "Unknown error"
  }
}


//#region Response Data
/**
 * Base class for response data.
 */
export abstract class WebSocketResponseData {
  message: string
  protected result?: any
  constructor(data: any) {
    this.message = data.message
  }
}

/**
 * Response data for the {@link ClearNotificationCommand}.
 */
export class ClearNotificationData extends WebSocketResponseData {}


/**
 * Response data for the {@link GetNotificationsCommand}.
 */
export class GetNotificationsData extends WebSocketResponseData {
  result: Notification[]
  constructor(data: any) {
    super(data)
    this.result = data.result.map((n: any) => Notification.handleNotification(n))
  }
}

/**
 * Response data for the {@link ClearNotificationsCommand}.
 */
export class ClearNotificationsData extends WebSocketResponseData {
  result: Notification[]
  constructor(data: any) {
    super(data)
    this.result = data.result.map((n: any) => Notification.handleNotification(n))
  }
}


/**
 * Response data for the {@link ClearAllNotificationsCommand}.
 */
export class ClearAllNotificationsData extends WebSocketResponseData {}


/**
 * Response data for the {@link ForceClearNotificationsCommand}.
 */
export class ForceClearNotificationsData extends WebSocketResponseData {
  result: Notification[]
  constructor(data: any) {
    super(data)
    this.result = data.result.map((n: any) => Notification.handleNotification(n))
  }
}


/**
 * Response data for the {@link ForceClearAllNotificationsCommand}.
 */
export class ForceClearAllNotificationsData extends WebSocketResponseData {}


/**
 * Response data for the {@link SetNotificationSeenCommand}.
 */
export class SetNotificationSeenData extends WebSocketResponseData {}

/**
 * Response data for the {@link FollowUserCommand}.
 */
export class FollowUserData extends WebSocketResponseData {}

/**
 * Response data for the {@link UnfollowUserCommand}.
 */
export class UnfollowUserData extends WebSocketResponseData {}

/**
 * Response data for the {@link GetFollowersCommand}.
 * @property result The number of users following the current user.
 */
export class GetFollowersData extends WebSocketResponseData {
  result: number
  constructor(data: any) {
    super(data)
    this.result = data.result
  }
}

/**
 * Response data for the {@link GetFollowingCommand}.
 * @property result The users the current user is following.
 */
export class GetFollowingData extends WebSocketResponseData {
  result: UserDto[]
  constructor(data: any) {
    super(data)
    this.result = data.result.map((u: any) => new UserDto(u))
  }
}

/**
 * Response data for the {@link ForceFollowUserCommand}.
 */
export class ForceFollowUserData extends WebSocketResponseData {}

/**
 * Response data for the {@link ForceUnfollowUserCommand}.
 */
export class ForceUnfollowUserData extends WebSocketResponseData {}

/**
 * Response data for the {@link AddRoleToUserCommand}.
 */
export class AddRoleToUserData extends WebSocketResponseData {}

/**
 * Response data for the {@link RemoveRoleFromUserCommand}.
 */
export class RemoveRoleFromUserData extends WebSocketResponseData {}
//#endregion