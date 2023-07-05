import { Endpoint, HttpMethod } from "../Endpoints"
import * as Responses from "../ApiResponse"

export const ROOT_PATH = "/oauth"

export class OAuthRegisterEndpoint extends Endpoint<Responses.UserLoginResponse> {
  constructor(code: string) {
    // Doesn't really neec credentials, but it's required to allow Set-Cookie
    super(Responses.UserLoginResponse, ROOT_PATH + `/${code}`, HttpMethod.POST, true)
  }
}

export class OAuthLoginEndpoint extends Endpoint<Responses.UserLoginResponse> {
  constructor() {
    super(Responses.UserLoginResponse, ROOT_PATH + "/login", HttpMethod.GET, true)
  }
}

export class OAuthLogoutEndpoint extends Endpoint<Responses.EmptyResponse> {
  constructor() {
    super(Responses.EmptyResponse, ROOT_PATH + "/logout", HttpMethod.GET, true)
  }
}