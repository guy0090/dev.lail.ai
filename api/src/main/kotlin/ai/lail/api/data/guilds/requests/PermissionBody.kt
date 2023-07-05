package ai.lail.api.data.guilds.requests

import java.io.Serializable

data class PermissionBody(
    val permissions: List<String>,
) : Serializable
