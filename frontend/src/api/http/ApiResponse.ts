import * as Requests from '@/api/common/dto/ResponseDtos'
import type { AxiosResponse } from 'axios'

export abstract class ApiResponse<T = any> {
  protected data?: T
  protected clazz?: new (data: any) => T

  constructor(builder?: new (data: any) => T, data?: any) {
    this.clazz = builder
    if (this.clazz && data) this.data = new this.clazz(data)
  }


  getResult(): T {
    if (!this.data) throw Error('No response data')
    return this.data
  }
}

export class EmptyResponse extends ApiResponse<AxiosResponse> {
  response: AxiosResponse
  constructor(response: AxiosResponse) {
    super()
    this.response = response
  }

  getResult(): AxiosResponse {
    return this.response
  }
}

//#region User Responses
export class UserResponse extends ApiResponse<Requests.UserDto> {
  constructor(data: any) {
    super(Requests.UserDto, data)
  }
}

export class UsersResponse extends ApiResponse<Requests.UsersDto> {
  constructor(data: any) {
    super(Requests.UsersDto, data)
  }
}

export class UserProfileResponse extends ApiResponse<Requests.UserProfileDto> {
  constructor(data: any) {
    super(Requests.UserProfileDto, data)
  }
}

/// Self Responses
export class UserSelfResponse extends ApiResponse<Requests.UserSelfDto> {
  constructor(data: any) {
    super(Requests.UserSelfDto, data)
  }
}

export class UserSettingsResponse extends ApiResponse<Requests.UserSettingsDto> {
  constructor(data: any) {
    super(Requests.UserSettingsDto, data)
  }
}

export class GetSelfPermissionsResponse extends ApiResponse<Requests.GetSelfPermissionsDto> {
  constructor(data: any) {
    super(Requests.GetSelfPermissionsDto, data)
  }
}

/// Admin user responses
export class AdminUserResponse extends ApiResponse<Requests.AdminUserDto> {
  constructor(data: any) {
    super(Requests.AdminUserDto, data)
  }
}
//#endregion

//#region OAuth Responses
export class UserLoginResponse extends ApiResponse<Requests.UserDetailsDto> {
  constructor(data: any) {
    super(Requests.UserDetailsDto, data)
  }
}
//#endregion

//#region Encounter Responses
export class EncounterCountResponse extends ApiResponse<Requests.EncounterCountDto> {
  constructor(data: any) {
    super(Requests.EncounterCountDto, data)
  }
}

export class EncounterEntryResponse extends ApiResponse<Requests.EncounterEntryDto> {
  constructor(data: any) {
    super(Requests.EncounterEntryDto, data)
  }
}

export class EncounterSummaryResponse extends ApiResponse<Requests.EncounterSummaryDto> {
  constructor(data: any) {
    super(Requests.EncounterSummaryDto, data)
  }
}

export class TopAndRecentEncountersResponse extends ApiResponse<Requests.TopAndRecentEncountersDto> {
  constructor(data: any) {
    super(Requests.TopAndRecentEncountersDto, data)
  }
}

export class RecentEncountersResponse extends ApiResponse<Requests.RecentEncountersDto> {
  constructor(data: any) {
    super(Requests.RecentEncountersDto, data)
  }
}
//#endregion