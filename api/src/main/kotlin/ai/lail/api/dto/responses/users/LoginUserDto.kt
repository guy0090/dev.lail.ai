package ai.lail.api.dto.responses.users

import ai.lail.api.data.permissions.Role
import ai.lail.api.data.settings.user.UserSettings
import ai.lail.api.data.users.User
import ai.lail.api.dto.responses.roles.RoleDto

class LoginUserDto(
    user: User,
    settings: UserSettings,
    roles: List<Role>,
    val permissions: Set<String>,
    val token: String,
    val uploads: Int
) {
    val user: UserSelfDto = UserSelfDto(user, uploads)
    val settings: UserSettingsDto = UserSettingsDto(settings)
    val roles: List<RoleDto> = roles.map { RoleDto(it) }
}