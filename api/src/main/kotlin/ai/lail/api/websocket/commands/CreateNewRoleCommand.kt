package ai.lail.api.websocket.commands

import ai.lail.api.dto.requests.roles.CreateRoleDto
import ai.lail.api.exceptions.AbstractException
import ai.lail.api.services.RoleService
import ai.lail.api.websocket.CommandContent
import ai.lail.api.websocket.WebSocketCommand
import ai.lail.api.websocket.WebSocketEvent
import ai.lail.api.websocket.WebSocketResponse
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.context.ApplicationContext

class CreateNewRoleCommand(body: CommandContent) : WebSocketCommand(body) {
    override val event: WebSocketEvent = WebSocketEvent.CreateNewRole
    private val roleId = jacksonObjectMapper().readValue(body.content.toString(), CreateRoleDto::class.java)
    override fun handle(response: WebSocketResponse, context: ApplicationContext) {
        val content = ObjectMapper().createObjectNode()
        try {
            val roleService = context.getBean(RoleService::class.java)
            val newRole = roleService.createNewRole(roleId)
            content.put("message", "Created a new role: $newRole")
            content.putPOJO("result", newRole)
            response.complete(content)
        } catch (e: Exception) {
            if (e is AbstractException) content.put("message", e.message)
            else content.put("message", "An unknown error occurred")
            response.fail(content)
        }
    }
}