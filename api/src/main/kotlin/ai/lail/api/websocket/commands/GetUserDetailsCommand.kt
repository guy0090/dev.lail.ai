package ai.lail.api.websocket.commands

import ai.lail.api.exceptions.AbstractException
import ai.lail.api.services.AdminService
import ai.lail.api.websocket.CommandContent
import ai.lail.api.websocket.WebSocketCommand
import ai.lail.api.websocket.WebSocketEvent
import ai.lail.api.websocket.WebSocketResponse
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.ApplicationContext

class GetUserDetailsCommand(body: CommandContent) : WebSocketCommand(body) {
    override val event: WebSocketEvent = WebSocketEvent.GetUserDetails
    private val targetId = getObjectId("targetId")

    override fun handle(response: WebSocketResponse, context: ApplicationContext) {
        val content = ObjectMapper().createObjectNode()
        try {
            val adminService = context.getBean(AdminService::class.java)
            val details = adminService.getUserDetails(targetId)
            content.put("message", "Got user details")
            content.putPOJO("result", details)
            response.complete(content)
        } catch (e: Exception) {
            if (e is AbstractException) content.put("message", e.message)
            else content.put("message", "An unknown error occurred")
            response.fail(content)
        }
    }
}