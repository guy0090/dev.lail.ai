import { SOCKET_HOST } from "@/api/http/Api"
import { Endpoint } from "../Endpoints"
import * as Responses from "../ApiResponse"

// Unused
export class WebSocketEndpoint extends Endpoint<Responses.EmptyResponse> {
  constructor(token?: string) {
    if (token) {
      super(Responses.EmptyResponse, "")
      this.path = `${SOCKET_HOST}/events?token=${token}`
    } else {
      super(Responses.EmptyResponse, "")
      this.path = `${SOCKET_HOST}/events`
    }
  }
}