package ai.lail.api.dto.requests.users

import ai.lail.api.enums.UpdateType
import jakarta.validation.Valid

data class UserDetailsUpdateDto(
    var permissions: Set<String>? = null,
    var roles: Set<String>? = null,
    var type: UpdateType? = null,
    @Valid
    var settings: UserSettingsUpdateDto? = null,
)