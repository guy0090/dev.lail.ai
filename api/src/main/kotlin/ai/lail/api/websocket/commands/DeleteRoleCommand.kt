package ai.lail.api.websocket.commands

import ai.lail.api.exceptions.AbstractException
import ai.lail.api.services.PermissionService
import ai.lail.api.websocket.CommandContent
import ai.lail.api.websocket.WebSocketCommand
import ai.lail.api.websocket.WebSocketEvent
import ai.lail.api.websocket.WebSocketResponse
import com.fasterxml.jackson.databind.ObjectMapper
import org.bson.types.ObjectId
import org.springframework.context.ApplicationContext

class DeleteRoleCommand(body: CommandContent) : WebSocketCommand(body) {
    override val event: WebSocketEvent = WebSocketEvent.DeleteRole
    private val roleId: ObjectId = getObjectId("roleId")

    override fun handle(response: WebSocketResponse, context: ApplicationContext) {
        val content = ObjectMapper().createObjectNode()
        try {
            val permissionService = context.getBean(PermissionService::class.java)
            permissionService.deleteRole(roleId)
            content.put("message", "Deleted $roleId")
            response.complete(content)
        } catch (e: Exception) {
            if (e is AbstractException) content.put("message", e.message)
            else content.put("message", "An unknown error occurred")
            response.fail(content)
        }
    }
}