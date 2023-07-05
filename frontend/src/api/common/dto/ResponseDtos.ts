import type { EncounterSummaryStatus } from '../enums/EncounterSummary'
import type PrivacySetting from '../enums/PrivacySetting'
import type { DamageStatistics, EncounterAssociation, EncounterEntity, EncounterParticipant } from '../interfaces/Encounters'

/**
 * Represents a user in the system.
 *
 * @property {string} id The user's ID
 * @property {string} username The user's username
 * @property {string} avatar The user's avatar
 * @property {string} discordId The user's Discord ID
 * @property {string} discriminator The user's Discord discriminator
 * @property {number} lastSeen The user's last seen timestamp
 */
export class UserDto {
  id: string
  username: string
  avatar?: string
  discordId: string
  discriminator: string
  lastSeen: number

  constructor(user: any) {
    this.id = user.id
    this.username = user.username
    this.avatar = user.avatar
    this.discordId = user.discordId
    this.discriminator = user.discriminator
    this.lastSeen = user.lastSeen
  }
}

/**
 * Utility class for endpoints returning many users
 */
export class UsersDto {
  users: UserDto[]

  constructor(data: any[]) {
    this.users = data.map((user: any) => new UserDto(user))
  }
}

/**
 * Represents the local (logged in) user in the system.
 */
export class UserSelfDto {
  id: string
  discordId: string
  discordUsername: string
  discriminator: string
  avatar: string
  registeredDate: number
  lastSeen: number

  constructor(user: any) {
    this.id = user.id
    this.discordId = user.discordId
    this.discordUsername = user.discordUsername
    this.discriminator = user.discriminator
    this.avatar = user.avatar
    this.registeredDate = user.registeredDate
    this.lastSeen = user.lastSeen
  }
}

/**
 * Data transfer object for the local user's details including multiple
 * related objects. Only received on initial login.
 *
 * @property {UserSelfDto} user The user object
 * @property {UserSettingsDto} settings The user's settings
 * @property {BasicRoleDto[]} roles The user's roles
 * @property {string[]} permissions The user's permissions
 * @property {string} token The user's JWT token
 */
export class UserDetailsDto {
  user: UserSelfDto
  settings: UserSettingsDto
  roles: BasicRoleDto[]
  permissions: string[]
  token: string

  constructor(loginUser: any) {
    this.user = loginUser.user
    this.settings = new UserSettingsDto(loginUser.settings)
    this.roles = loginUser.roles.map((role: any) => new BasicRoleDto(role))
    this.permissions = loginUser.permissions
    this.token = loginUser.token
  }
}

/**
 * Base class for profiles.
 *
 * @property {boolean} isLocalUser Whether the profile is for the local user
 * @property {any[]} badges The user's badges
 */
export abstract class Profile {
  isLocalUser: boolean
  badges?: any[]
  roles: BasicRoleDto[]

  abstract user: UserDto | UserSelfDto

  constructor(data: any) {
    this.badges = []
    this.roles = (data.roles ?? []).map((role: any) => new BasicRoleDto(role))
    this.isLocalUser = false
  }
}

/**
 * User profile of the local user.
 *
 * @property {UserSelfDto} user The user object
 * @property {BasicRoleDto[]} roles The user's roles
 * @property {UserSettingsDto} settings The user's settings
 */
export class UserSelfProfileDto extends Profile {
  user: UserSelfDto
  settings?: UserSettingsDto
  following: UserDto[]

  constructor(data: any) {
    super(data)
    this.user = new UserSelfDto(data.user)
    this.following = data.following.map((user: any) => new UserDto(user))
    this.settings = new UserSettingsDto(data.settings)
    this.isLocalUser = true
  }

  static fromUserState(
    user: UserSelfDto,
    roles: BasicRoleDto[],
    settings: UserSettingsDto,
    following?: UserDto[]
  ) {
    return new UserSelfProfileDto({
      user,
      roles,
      settings,
      following: following || [],
    })
  }
}

/**
 * User profile of another user.
 *
 * @property {UserDto} user The user object
 */
export class UserProfileDto extends Profile {
  user: UserDto
  followers: number
  constructor(data: any) {
    super(data)
    this.user = new UserDto(data.user)
    this.followers = data.followers || 0
  }
}

/**
 * Represents a user's settings.
 */
export class UserSettingsDto {
  username?: string
  profilePrivacySetting: PrivacySetting
  uploadPrivacySetting: PrivacySetting
  customUrlSlug?: string
  profileColor?: string
  region?: string
  server?: string

  constructor(data: any) {
    this.username = data.username
    this.profilePrivacySetting = data.profilePrivacySetting
    this.uploadPrivacySetting = data.uploadPrivacySetting
    this.customUrlSlug = data.customUrlSlug
    this.profileColor = data.profileColor || ''
    this.region = data.region
    this.server = data.server
  }
}

/**
 * Represents a role in the system. Trimmed to only include the name, color, and icon.
 *
 * @property {string} name The name of the role
 * @property {string} color The color of the role
 * @property {string} icon The icon of the role
 *
 * @param {any} role The role object
 */
export class BasicRoleDto {
  name: string
  color?: string
  icon?: string
  hidden: boolean

  constructor(role: any) {
    this.name = role.name
    this.color = role.color
    this.icon = role.icon
    this.hidden = role.hidden ?? false
  }
}

/**
 * Represents all permissions a user has
 */
export class GetSelfPermissionsDto {
  permissions: string[]
  constructor(data: any) {
    this.permissions = data.permissions
  }
}

/**
 * Represents a user object without any truncated fields
 */
export class UserFullDto extends UserDto {
  discordUsername: string
  registeredDate: number
  banned: boolean
  lastDiscordUpdate: number
  blockedUsers: UserDto[]

  constructor(data: any) {
    super(data)
    this.discordUsername = data.discordUsername
    this.registeredDate = data.registeredDate
    this.banned = data.banned
    this.lastDiscordUpdate = data.lastDiscordUpdate
    this.blockedUsers = data.blockedUsers.map((u: any) => new UserDto(u))
  }
}

/**
 * Represents a user response from admin endpoints
 */
export class AdminUserDto {
  user: UserFullDto
  permissions: string[]
  roles: BasicRoleDto[]
  settings: UserSettingsDto

  constructor(data: any) {
    this.user = new UserFullDto(data.user)
    this.permissions = data.permissions
    this.roles = data.roles.map((r: any) => new BasicRoleDto(r))
    this.settings = new UserSettingsDto(data.settings)
  }
}

/**
 * Represents the amount of encounters a user is owner of and the latest
 * encounter they have uploaded.
 */
export class EncounterCountDto {
  count: number
  latest?: EncounterSummaryDto

  constructor(data: any) {
    this.count = data.count
    this.latest = data.latest ? new EncounterSummaryDto(data.latest) : undefined
  }
}

/**
 * Represents the response from the non-specific recent encounters endpoint.
 *
 * Includes the daily top DPS and the most recent encounters.
 */
export class TopAndRecentEncountersDto {
  top?: EncounterSummaryDto
  recents: EncounterSummaryDto[]

  constructor(data: any) {
    this.top = data.top ? new EncounterSummaryDto(data.top) : undefined
    this.recents = data.recents.map((e: any) => new EncounterSummaryDto(e))
  }
}

/**
 * Represents the response from the latest encounters endpoint.
 */
export class RecentEncountersDto {
  recents: EncounterSummaryDto[]

  constructor(data: any) {
    this.recents = data.map((e: any) => new EncounterSummaryDto(e))
  }
}

/**
 * Represents the response from the encounter summary endpoint. Is also shared
 * with most recent encounters endpoints.
 */
export class EncounterSummaryDto {
  id: string;
  visibility: PrivacySetting;
  association: EncounterAssociation;
  owner: string;
  users: string[];
  bossId: number;
  participants: EncounterParticipant[];
  duration: number;
  created: number;
  status: EncounterSummaryStatus;

  constructor(data: any) {
    this.id = data.id
    this.visibility = data.visibility
    this.association = data.association
    this.owner = data.owner
    this.users = data.users
    this.bossId = data.bossId
    this.participants = data.participants
    this.duration = data.duration
    this.created = data.created
    this.status = data.status
  }
}

export class EncounterDto {
  startedOn: number
  lastCombatPacket: number
  fightStartedOn: number
  currentBoss: string
  entities: EncounterEntity[]
  damageStatistics: DamageStatistics

  constructor(data: any) {
    this.startedOn = data.startedOn
    this.lastCombatPacket = data.lastCombatPacket
    this.fightStartedOn = data.fightStartedOn
    this.currentBoss = data.currentBoss
    this.entities = data.entities
    this.damageStatistics = data.damageStatistics
  }

  getDuration(): number {
    return this.lastCombatPacket - this.fightStartedOn
  }
}

export class EncounterEntryDto {
  summary: EncounterSummaryDto
  encounter: EncounterDto
  related?: EncounterSummaryDto[]

  constructor(data: any) {
    this.summary = new EncounterSummaryDto(data.summary)
    this.encounter = new EncounterDto(data.encounter)
    this.related = data.related?.map((e: any) => new EncounterSummaryDto(e))
  }
}

export class ParticipantDto {
  user: UserDto
  participant: EncounterParticipant

  constructor(user: UserDto, participant: EncounterParticipant) {
    this.user = user
    this.participant = participant
  }
}