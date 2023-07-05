import PrivacySetting from "@/api/common/enums/PrivacySetting";
import { AppException, AppExceptionCode } from "@/common/AppException";

/**
 * DTO to view a single user
 */
export class UserViewDto {
  id?: string
  slug?: string


  constructor(id: string) {
    if (id.length === 24) this.id = id
    else this.slug = id
  }

  static slugRgx: RegExp = /^[a-zA-Z0-9_]{2,20}$/

  static fromSlug(slug: string): UserViewDto {
    if (!this.slugRgx.test(slug)) throw new AppException(AppExceptionCode.InvalidSlug)
    return new UserViewDto(slug)
  }

  static fromId(id: string): UserViewDto {
    if (id.length !== 24) throw new AppException(AppExceptionCode.InvalidObjectId)
    return new UserViewDto(id)
  }
}

/**
 * DTO to view multiple users
 */
export class UsersViewDto {
  userIds: string[]

  constructor(userIds: string[]) {
    if (!userIds.every(id => id.length === 24)) throw new AppException(AppExceptionCode.InvalidObjectIds)
    this.userIds = userIds
  }
}

/**
 * Utility class for endpoints requiring an ObjectId
 */
export class ObjectIdDto {
  id: string

  constructor(id: string) {
    if (id.length !== 24) throw new AppException(AppExceptionCode.InvalidObjectId)
    this.id = id
  }
}

/**
 * DTO to update user settings
 */
export class UserSettingsUpdateDto {
  username?: string
  profilePrivacySetting?: PrivacySetting
  uploadPrivacySetting?: PrivacySetting
  customUrlSlug?: string
  profileColor?: string
  region?: string
  server?: string

  constructor(data: any) {
    if (data.username) this.username = data.username
    if (data.profilePrivacySetting !== undefined) this.profilePrivacySetting = data.profilePrivacySetting
    if (data.uploadPrivacySetting !== undefined) this.uploadPrivacySetting = data.uploadPrivacySetting
    if (data.customUrlSlug) this.customUrlSlug = data.customUrlSlug
    if (data.profileColor) this.profileColor = data.profileColor
    if (data.region) this.region = data.region
    if (data.server) this.server = data.server
  }
}

/**
 * DTO for resetting user settings
 */
export class UserSettingsResetDto {
  username?: boolean
  profilePrivacySetting?: boolean
  uploadPrivacySetting?: boolean
  customUrlSlug?: boolean
  profileColor?: boolean
  region?: boolean
  server?: boolean

  constructor(data: any) {
    this.username = data.username || false
    this.profilePrivacySetting = data.profilePrivacySetting || false
    this.uploadPrivacySetting = data.uploadPrivacySetting || false
    this.customUrlSlug = data.customUrlSlug || false
    this.profileColor = data.profileColor || false
    this.region = data.region || false
    this.server = data.server || false
  }
}

/**
 * DTO for requesting an encounter
 */
export class EncounterViewDto {
  id: string
  showNames?: boolean
  privacy?: PrivacySetting[]

  constructor(id: string, showNames?: boolean, privacy?: PrivacySetting[]) {
    this.id = id
    this.showNames = showNames
    this.privacy = privacy ?? [PrivacySetting.PUBLIC, PrivacySetting.UNLISTED]
  }
}

/**
 * DTO for requests going to specific recent encounter endpoints
 *
 * @property {number} page The page of encounters to get
 * @property {number} limit The number of encounters to get per page
 * @property {boolean} showNames Whether or not to show the names of the users in the encounter
 */
export class EncounterViewRecentDto {
  page: number
  limit: number
  showNames?: boolean

  constructor(page: number, limit: number, showNames?: boolean) {
    this.page = page
    this.limit = limit
    this.showNames = showNames
  }
}