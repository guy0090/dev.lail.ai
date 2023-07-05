package ai.lail.api.websocket.events

import ai.lail.api.dto.responses.roles.RoleDto
import ai.lail.api.dto.responses.users.UserSettingsDto
import ai.lail.api.websocket.WebSocketEvent
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper

data class UserDetailsChangedEvent(
    @JsonProperty("permissions")
    @JsonInclude(JsonInclude.Include.NON_NULL)

    var permissions: Set<String>? = null,
    @JsonProperty("roles")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var roles: List<RoleDto>? = null,

    @JsonProperty("settings")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var settings: UserSettingsDto? = null,
) : AbstractWebSocketEvent() {
    override val event: WebSocketEvent = WebSocketEvent.UserDetailsChanged

    override fun getResponse(): JsonNode {
        val response = ObjectMapper().createObjectNode()
        response.putPOJO("permissions", permissions)
        response.putPOJO("roles", roles)
        response.putPOJO("settings", settings)
        return response
    }
}