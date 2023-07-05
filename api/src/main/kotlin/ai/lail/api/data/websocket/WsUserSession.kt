package ai.lail.api.data.websocket

import org.bson.types.ObjectId
import org.springframework.web.socket.WebSocketSession

data class WsUserSession(
    var session: WebSocketSession,
    var userId: ObjectId? = null // The user's Mongo ID
)