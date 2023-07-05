package ai.lail.api.data.guilds.requests

import java.io.Serializable

data class RoleCreateBody(
    val name: String,
    val color: String,
    val permissions: List<String>
) : Serializable