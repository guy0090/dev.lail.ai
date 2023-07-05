package ai.lail.api.websocket.commands

import ai.lail.api.exceptions.AbstractException
import ai.lail.api.exceptions.common.MissingAuthPrincipalException
import ai.lail.api.exceptions.websocket.InvalidWsContentException
import ai.lail.api.services.NotificationService
import ai.lail.api.websocket.CommandContent
import ai.lail.api.websocket.WebSocketCommand
import ai.lail.api.websocket.WebSocketEvent
import ai.lail.api.websocket.WebSocketResponse
import com.fasterxml.jackson.databind.ObjectMapper
import org.bson.types.ObjectId
import org.springframework.context.ApplicationContext

class SetNotificationSeenCommand(body: CommandContent) : WebSocketCommand(body) {
    override val event: WebSocketEvent = WebSocketEvent.SetNotificationSeen
    val user by lazy { sender ?: throw MissingAuthPrincipalException() }
    private val targetId: ObjectId = getObjectId("targetId")

    override fun handle(response: WebSocketResponse, context: ApplicationContext) {
        val content = ObjectMapper().createObjectNode()
        try {
            val notificationService = context.getBean(NotificationService::class.java)
            notificationService.setNotificationSeen(user.id, targetId)
            content.put("message", "Set notification $targetId as seen")
            response.complete(content)
        } catch (e: Exception) {
            if (e is AbstractException) content.put("message", e.message)
            else content.put("message", "An unknown error occurred")
            response.fail(content)
        }
    }
}