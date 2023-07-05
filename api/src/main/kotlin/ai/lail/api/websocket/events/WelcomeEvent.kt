package ai.lail.api.websocket.events

import ai.lail.api.websocket.WebSocketEvent
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper

data class WelcomeEvent(
    val version: String?,
    val encountersCount: Long?,
    val backendReady: Boolean
) : AbstractWebSocketEvent() {
    override val event: WebSocketEvent = WebSocketEvent.Welcome

    override fun getResponse(): JsonNode {
        val response = ObjectMapper().createObjectNode()
        response.put("version", version)
        response.put("encounters", encountersCount)
        response.put("backendReady", backendReady)
        return response
    }
}