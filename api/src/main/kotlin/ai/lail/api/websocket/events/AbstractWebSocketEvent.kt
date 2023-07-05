package ai.lail.api.websocket.events

import ai.lail.api.websocket.WebSocketEvent
import com.fasterxml.jackson.databind.JsonNode

abstract class AbstractWebSocketEvent {
    abstract val event: WebSocketEvent
    abstract fun getResponse(): JsonNode
}