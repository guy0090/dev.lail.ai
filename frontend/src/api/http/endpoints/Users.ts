import { Endpoint, HttpMethod } from '../Endpoints'
import * as Responses from '../ApiResponse'
import type * as Requests from '@/api/common/dto/RequestDtos'

const ROOT_PATH = '/users'
const ME_ROOT = ROOT_PATH + '/me'

export class GetUserEndpoint extends Endpoint<Responses.UserResponse> {
  constructor(dto: Requests.UserViewDto) {
    super(Responses.UserResponse, ROOT_PATH + "/", HttpMethod.POST, true, dto)
    this.headers = { 'Content-Type': 'application/json' }
  }
}

export class GetUsersEndpoint extends Endpoint<Responses.UsersResponse> {
  constructor(dto: Requests.UsersViewDto) {
    super(Responses.UsersResponse, ROOT_PATH + '/many', HttpMethod.POST, true, dto)
    this.headers = { 'Content-Type': 'application/json' }
  }
}

export class GetProfileEndpoint extends Endpoint<Responses.UserProfileResponse> {
  constructor(dto: Requests.UserViewDto) {
    super(Responses.UserProfileResponse, ROOT_PATH + '/profile', HttpMethod.POST, false, dto)
    this.headers = { 'Content-Type': 'application/json' }
  }
}

//#region Self endpoints
export class GetSelfEndpoint extends Endpoint<Responses.UserSelfResponse> {
  constructor() {
    super(Responses.UserSelfResponse, ME_ROOT, HttpMethod.GET, true)
  }
}

export class GetSelfSettingsEndpoint extends Endpoint<Responses.UserSettingsResponse> {
  constructor() {
    super(Responses.UserSettingsResponse, ME_ROOT + '/settings', HttpMethod.GET, true)
  }
}

export class UpdateSelfSettingsEndpoint extends Endpoint<Responses.UserSettingsResponse> {
  constructor(dto: Requests.UserSettingsUpdateDto) {
    super(Responses.UserSettingsResponse, ME_ROOT + '/settings', HttpMethod.PATCH, true, dto)
    this.headers = { 'Content-Type': 'application/json' }
  }
}

export class ResetSelfSettingsEndpoint extends Endpoint<Responses.UserSettingsResponse> {
  constructor(dto: Requests.UserSettingsResetDto) {
    super(Responses.UserSettingsResponse, ME_ROOT + '/settings', HttpMethod.PUT, true, dto)
    this.headers = { 'Content-Type': 'application/json' }
  }
}

export class GetSelfPermissionsEndpoint extends Endpoint<Responses.GetSelfPermissionsResponse> {
  constructor() {
    super(Responses.GetSelfPermissionsResponse, ME_ROOT + '/permissions', HttpMethod.GET, true)
    this.headers = { 'Content-Type': 'application/json' }
  }
}

export class SelfHasPermissionEndpoint extends Endpoint<Responses.EmptyResponse> {
  constructor(permission: string) {
    super(Responses.EmptyResponse, ME_ROOT + '/permissions', HttpMethod.POST, true, permission)
    this.headers = { 'Content-Type': 'text/plain' }
    // Don't throw on 403
    this.validateStatus = (status: number) => status === 200 || status === 403
  }
}
//#endregion
