import { HttpMethod, Endpoint } from "@/api/http/Endpoints";
import * as Responses from "@/api/http/ApiResponse"
import { ADMIN_ROOT } from "./Common";

export const SYSTEM_ROOT = ADMIN_ROOT + "/system";

export class InitInstanceEndpoint extends Endpoint<Responses.EmptyResponse> {
  constructor(initKey: string) {
    super(Responses.EmptyResponse, SYSTEM_ROOT + "/init", HttpMethod.POST, true, initKey);
    this.headers = { 'Content-Type': 'text/plain' }
  }
}