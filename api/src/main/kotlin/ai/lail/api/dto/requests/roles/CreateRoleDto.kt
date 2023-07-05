package ai.lail.api.dto.requests.roles

import org.bson.types.ObjectId
import java.io.Serializable

data class CreateRoleDto(
    var name: String,
    var permissions: Set<String> = emptySet(),
    var inherits: Set<ObjectId> = emptySet(),
    var icon: String? = null,
    var color: String? = null,
    var hidden: Boolean = false
) : Serializable
