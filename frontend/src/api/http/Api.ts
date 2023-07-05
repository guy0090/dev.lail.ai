import type { ApiResponse } from './ApiResponse'
import type { Endpoint } from './Endpoints'
import * as Requests from '../common/dto/RequestDtos'
import * as Users from './endpoints/Users'
import * as OAuth from './endpoints/OAuth'
import * as Encounters from './endpoints/Encounters'
import * as System from './endpoints/admin/System'
import type PrivacySetting from '../common/enums/PrivacySetting'

export const SOCKET_HOST = import.meta.env.VITE_SOCKET_HOST || 'ws://localhost:8080'
export const SITE_HOST = import.meta.env.VITE_SITE_HOST || 'http://localhost:7070'
export const API_HOST = import.meta.env.VITE_API_HOST || 'http://localhost:8080'
export const CLIENT_ID = import.meta.env.VITE_CLIENT_ID

export const getRedirectUrl = () => {
  const hostEncoded = encodeURIComponent(`${SITE_HOST}/login`) // ? Callback URI for Discord OAuth
  return `https://discord.com/api/oauth2/authorize?client_id=${CLIENT_ID}&redirect_uri=${hostEncoded}&response_type=code&scope=identify`
}

export const toDiscordOAuth = () => {
  window.location.href = getRedirectUrl()
}

export const sendApiRequest = async <T extends ApiResponse>(req: Endpoint<T>): Promise<T> => {
  return await req.sendRequest()
}

//#region System Requests
/// * System requests

/**
 * Initialize the instance with the key generated on first startup
 * @param key The key to use to initialize the instance
 * @returns Axios response
 */
export const initInstance = async (key: string) => {
  const endpoint = new System.InitInstanceEndpoint(key)
  const request = await sendApiRequest(endpoint)
  return request.getResult()
}
//#endregion System Requests

//#region User Requests
/// * User requests

/**
 * Get user by id or slug
 *
 * The ID or slug is validated to be a valid user ID or slug both locoally
 * prior to sending and on the server. If the ID or slug is invalid, the
 * request will fail.
 *
 * @throws Error if the ID or slug is invalid
 * @throws ApiException if the request fails for any other reason
 *
 * @param id User id or slug
 * @returns User
 */
export const getUser = async (id: string) => {
  const userDto = new Requests.UserViewDto(id)
  const endpoint = new Users.GetUserEndpoint(userDto)
  const request = await sendApiRequest(endpoint)
  return request.getResult()
}

/**
 * Get users by ids
 *
 * @throws Error if any of the IDs are invalid
 * @throws ApiException if the request fails for any other reason
 *
 * @param ids User ids
 * @returns Users
 */
export const getUsers = async (ids: string[]) => {
  const userDto = new Requests.UsersViewDto(ids)
  const endpoint = new Users.GetUsersEndpoint(userDto)
  const request = await sendApiRequest(endpoint)
  return request.getResult().users
}

/**
 * Get user profile by id or slug
 * @param id User id
 * @returns User profile
 */
export const getUserProfile = async (id: string) => {
  const userDto = new Requests.UserViewDto(id)
  const endpoint = new Users.GetProfileEndpoint(userDto)
  const request = await sendApiRequest(endpoint)
  return request.getResult()
}
//#endregion User Requests

//#region Self requests
/// * Self requests

/**
 * Get user for the currently logged in user
 * @returns User
 */
export const getSelf = async () => {
  const endpoint = new Users.GetSelfEndpoint()
  const request = await sendApiRequest(endpoint)
  return request.getResult()
}

/**
 * Get user profile for the currently logged in user
 * @returns User settings
 */
export const getSelfSettings = async () => {
  const endpoint = new Users.GetSelfSettingsEndpoint()
  const request = await sendApiRequest(endpoint)
  return request.getResult()
}

/**
 * Update user settings for the currently logged in user
 * @param dto User settings update dto
 * @returns Updated user settings
 */
export const updateSelfSettings = async (dto: Requests.UserSettingsUpdateDto) => {
  const endpoint = new Users.UpdateSelfSettingsEndpoint(dto)
  const request = await sendApiRequest(endpoint)
  return request.getResult()
}

/**
 * Reset user settings for the currently logged in user
 * @param dto User settings reset dto
 * @returns Updated user settings
 */
export const resetSelfSettings = async (dto: Requests.UserSettingsResetDto) => {
  const endpoint = new Users.ResetSelfSettingsEndpoint(dto)
  const request = await sendApiRequest(endpoint)
  return request.getResult()
}

/**
 * Get all permissions for the currently logged in user
 * @returns A list of permissions
 */
export const getSelfPermissions = async () => {
  const endpoint = new Users.GetSelfPermissionsEndpoint()
  const request = await sendApiRequest(endpoint)
  return request.getResult()
}

/**
 * Check if the currently logged in user has a permission
 * @param permission Permission to check
 * @returns True if the user has the permission, false otherwise
 */
export const selfHasPermission = async (permission: string) => {
  const endpoint = new Users.SelfHasPermissionEndpoint(permission)
  const request = await sendApiRequest(endpoint)
  return request.getResult().status === 200
}
//#endregion Self requests

//#region OAuth requests
/// * OAuth requests

/**
 * Register a user via Discord OAuth2
 * If the user already exists, the user will be logged in.
 *
 * @param code Code from Discord OAuth2
 * @returns User details
 */
export const register = async (code: string) => {
  const endpoint = new OAuth.OAuthRegisterEndpoint(code)
  const request = await sendApiRequest(endpoint)
  return request.getResult()
}

/**
 * Login a user and return their details
 * @returns User details
 */
export const login = async () => {
  const endpoint = new OAuth.OAuthLoginEndpoint()
  const request = await sendApiRequest(endpoint)
  return request.getResult()
}

/**
 * Logout the currently logged in user
 * @returns Axios response
 */
export const logout = async () => {
  const endpoint = new OAuth.OAuthLogoutEndpoint()
  const request = await sendApiRequest(endpoint)
  return request.getResult()
}
//#endregion OAuth requests

//#region Encounter requests
/// * Encounter requests

export const getEncounterEntry = async (id: string, showNames: boolean = false, privacy?: PrivacySetting[]) => {
  const dto = new Requests.EncounterViewDto(id, showNames, privacy)
  const endpoint = new Encounters.EncounterEntryEndpoint(dto)
  const request = await sendApiRequest(endpoint)
  return request.getResult()
}

export const getEncounterSummary = async (id: string, showName?: boolean) => {
  const dto = new Requests.EncounterViewDto(id, showName)
  const endpoint = new Encounters.EncounterSummaryEndpoint(dto)
  const request = await sendApiRequest(endpoint)
  return request.getResult()
}

export const countUserEncounters = async (userId: string, showName?: boolean) => {
  const dto = new Requests.EncounterViewDto(userId, showName)
  const endpoint = new Encounters.EncounterCountEndpoint(dto)
  const request = await sendApiRequest(endpoint)
  return request.getResult()
}

export const getTopAndRecentEncounters = async (showNames?: boolean) => {
  const dto = new Requests.EncounterViewRecentDto(0, 20, showNames)
  const endpoint = new Encounters.TopAndRecentEncountersEndpoint(dto)
  const request = await sendApiRequest(endpoint)
  return request.getResult()
}

export const getTopDpsEncounter = async (showNames?: boolean) => {
  const dto = new Requests.EncounterViewRecentDto(0, 1, showNames)
  const endpoint = new Encounters.TopDpsEncounterEndpoint(dto)
  const request = await sendApiRequest(endpoint)
  return request.getResult()
}

export const getLatestEncounters = async (page: number, pageSize: number, showNames?: boolean) => {
  const dto = new Requests.EncounterViewRecentDto(page, pageSize, showNames)
  const endpoint = new Encounters.LatestEncountersEndpoint(dto)
  const request = await sendApiRequest(endpoint)
  return request.getResult()
}
//#endregion Encounter requests