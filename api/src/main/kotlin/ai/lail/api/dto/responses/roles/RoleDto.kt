package ai.lail.api.dto.responses.roles

import ai.lail.api.data.permissions.Role
import java.io.Serializable

class RoleDto(role: Role) : Serializable {
    val name: String = role.name
    val color: String? = role.color
    val icon: String? = role.icon
    val hidden: Boolean = role.hidden
}