package ai.lail.api.data.guilds.requests

import java.io.Serializable

data class MemberRoleBody(
    val roles: List<String>
) : Serializable