package ai.lail.api.websocket.events

import ai.lail.api.data.notifications.Notification
import ai.lail.api.websocket.WebSocketEvent
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper

class NotificationEvent(private val notification: Notification) : AbstractWebSocketEvent() {
    override val event: WebSocketEvent = WebSocketEvent.Notification
    //private val subject: UserDto = sender

    override fun getResponse(): JsonNode {
        val response = ObjectMapper().createObjectNode()
        response.putPOJO("notification", notification)
        return response
    }
}