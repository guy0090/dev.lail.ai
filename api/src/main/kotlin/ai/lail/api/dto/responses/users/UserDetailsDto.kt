package ai.lail.api.dto.responses.users

import ai.lail.api.data.permissions.Role
import ai.lail.api.data.settings.user.UserSettings
import ai.lail.api.data.users.User


/**
 * Data transfer object for a detailed view of user data.
 *
 * @property user The user.
 * @property settings The user's settings.
 * @property permissions The user's permissions.
 */
data class UserDetailsDto(
    val user: UserSelfDto,
    val settings: UserSettings,
    val permissions: Set<String>,
    val roles: List<Role> = emptyList(),
)