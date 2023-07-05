package ai.lail.api.websocket.commands

import ai.lail.api.exceptions.AbstractException
import ai.lail.api.exceptions.common.MissingAuthPrincipalException
import ai.lail.api.services.NotificationService
import ai.lail.api.websocket.CommandContent
import ai.lail.api.websocket.WebSocketCommand
import ai.lail.api.websocket.WebSocketEvent
import ai.lail.api.websocket.WebSocketResponse
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.ApplicationContext

class GetNotificationsCommand(body: CommandContent) : WebSocketCommand(body) {
    override val event: WebSocketEvent = WebSocketEvent.GetNotifications
    val user by lazy { sender ?: throw MissingAuthPrincipalException() }

    override fun handle(response: WebSocketResponse, context: ApplicationContext) {
        val content = ObjectMapper().createObjectNode()
        try {
            val notificationService = context.getBean(NotificationService::class.java)
            val notifications = notificationService.getUserNotifications(user.id)
            content.put("message", "Got notifications for ${user.id}")
            content.putPOJO("result", notifications)
            response.complete(content)
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is AbstractException) content.put("message", e.message)
            else content.put("message", "An unknown error occurred")
            response.fail(content)
        }
    }
}