package ai.lail.api.dto.responses.users

import ai.lail.api.data.permissions.Role
import ai.lail.api.data.settings.user.UserSettings
import ai.lail.api.data.users.User
import ai.lail.api.dto.responses.roles.RoleDto

/**
 * Data transfer object for the user requesting their own data with details.
 * @property user The user requesting their own data.
 * @property settings The settings of the user.
 * @property permissions The permissions of the user.
 * @property roles The roles of the user.
 */
data class UserSelfDetailsDto(
    val user: UserSelfDto,
    val settings: UserSettingsDto,
    val permissions: Set<String>,
    val roles: List<RoleDto>
) {
    constructor(user: User, settings: UserSettings, permissions: Set<String>, roles: List<Role>, uploads: Int) : this(
        UserSelfDto(user, uploads),
        UserSettingsDto(settings),
        permissions,
        roles.map { RoleDto(it) }
    )
}