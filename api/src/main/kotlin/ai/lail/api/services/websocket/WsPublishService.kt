package ai.lail.api.services.websocket

import ai.lail.api.data.users.User
import ai.lail.api.services.PermissionService
import ai.lail.api.websocket.WebSocketEvent
import ai.lail.api.websocket.WebSocketResponse
import ai.lail.api.websocket.events.AbstractWebSocketEvent
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import org.bson.types.ObjectId
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession

@Service
class WsPublishService(
    val wsSessionService: WsSessionService,
    val permissionService: PermissionService,
) {
    val logger: Logger = LoggerFactory.getLogger(WsPublishService::class.java)


    fun publishToAll(event: AbstractWebSocketEvent) {
        for (session in wsSessionService.getSessions()) {
            sendMessage(session.session, event)
        }
    }


    /**
     * Publishes a message to all websocket sessions associated with a user
     */
    fun publishToUser(id: ObjectId, event: AbstractWebSocketEvent) {
        val userSessions = wsSessionService.getSessionByUserId(id)
        if (userSessions.isNotEmpty()) {
            userSessions.forEach {
                sendMessage(it.session, event)
            }
        }
    }


    /**
     * Publishes a message to the given WebSocket session.
     *
     * Permissions are not checked for this variant because it is assumed that the session is already
     * authenticated from previous handling of a command.
     */
    fun sendResponse(session: WebSocketSession, message: WebSocketResponse) {
        val content: ObjectNode = ObjectMapper().createObjectNode()
        val isError = message.event == WebSocketEvent.Error

        if (isError) content.put("event", message.event.id)
        else content.put("event", WebSocketEvent.Response.id)
        content.putPOJO("content", toJsonObject(message))

        synchronized(session) {
            session.sendMessage(TextMessage(content.toString()))
        }
    }


    fun sendMessage(session: WebSocketSession, message: AbstractWebSocketEvent) {
        val type = message.event
        val requiresPermission = type.requiresPermission()
        val userAttribute = session.attributes["user"] as? User

        if (requiresPermission && userAttribute == null) return
        else if (userAttribute != null) {
            val hasPermission = permissionService.hasPermission(userAttribute.id, type.permission)
            if (!hasPermission) return
        }

        val content: ObjectNode = ObjectMapper().createObjectNode()
        content.put("event", type.id)
        content.putPOJO("content", message.getResponse())

        synchronized(session) {
            session.sendMessage(TextMessage(content.toString()))
        }
    }


    fun toJsonObject(data: Any): ObjectNode {
        return try {
            ObjectMapper().valueToTree(data)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            throw e
        }
    }


    fun toJsonArray(data: Collection<*>): ArrayNode {
        return try {
            ObjectMapper().valueToTree(data)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            throw e
        }
    }
}