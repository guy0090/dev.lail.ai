import { ADMIN_ROOT } from "./Common";
import { Endpoint, HttpMethod } from "@/api/http/Endpoints";
import * as Responses from '@/api/http/ApiResponse'

export const USERS_ROOT = ADMIN_ROOT + "/users";

// TODO: Make admin user dto with additional fields
export class AdminGetUserEndpoint extends Endpoint<Responses.AdminUserResponse> {
  constructor(userId: string) {
    super(Responses.AdminUserResponse, USERS_ROOT + "/" + userId, HttpMethod.GET, true)
  }
}