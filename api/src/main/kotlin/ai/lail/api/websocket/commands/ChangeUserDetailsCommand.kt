package ai.lail.api.websocket.commands

import ai.lail.api.dto.requests.users.UserDetailsUpdateDto
import ai.lail.api.dto.responses.roles.RoleDto
import ai.lail.api.dto.responses.users.UserSettingsDto
import ai.lail.api.enums.UpdateType
import ai.lail.api.exceptions.AbstractException
import ai.lail.api.exceptions.permissions.PermissionsInvalidException
import ai.lail.api.exceptions.permissions.RoleNotFoundException
import ai.lail.api.exceptions.users.UserDetailsChangeException
import ai.lail.api.permissions.Permission
import ai.lail.api.services.PermissionService
import ai.lail.api.services.UserSettingsService
import ai.lail.api.services.websocket.WsPublishService
import ai.lail.api.util.ObjectIdHelper
import ai.lail.api.websocket.CommandContent
import ai.lail.api.websocket.WebSocketCommand
import ai.lail.api.websocket.WebSocketEvent
import ai.lail.api.websocket.WebSocketResponse
import ai.lail.api.websocket.events.UserDetailsChangedEvent
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.context.ApplicationContext

class ChangeUserDetailsCommand(body: CommandContent) : WebSocketCommand(body) {
    override val event: WebSocketEvent = WebSocketEvent.GetUserDetails
    private val targetId = getObjectId("targetId")
    private val update =
        jacksonObjectMapper().readValue(body.content.get("update").toString(), UserDetailsUpdateDto::class.java)

    override fun handle(response: WebSocketResponse, context: ApplicationContext) {
        val content = ObjectMapper().createObjectNode()
        try {
            val changeEvent = getUpdateEvent(context)
            val publishService = context.getBean(WsPublishService::class.java)
            publishService.publishToUser(targetId, changeEvent)

            content.put("message", "Changed user details")
            response.complete(content)
        } catch (e: Exception) {
            if (e is AbstractException) content.put("message", e.message)
            else content.put("message", "An unknown error occurred")
            response.fail(content)
        }
    }

    /**
     * Get the event that will be published to the user.
     *
     * Changes are made based on the update type and the updates provided (added or removed). If no
     * update type was provided but changes were set, an exception will be thrown.
     *
     * @param context The application context
     * @return The event to publish
     *
     * @see UpdateType
     * @see UserDetailsUpdateDto
     *
     * @throws UserDetailsChangeException If no update type was provided but changes were set
     * @throws PermissionsInvalidException If the permissions provided are invalid
     * @throws RoleNotFoundException If a role was not found
     */
    @Throws(UserDetailsChangeException::class, PermissionsInvalidException::class, RoleNotFoundException::class)
    private fun getUpdateEvent(context: ApplicationContext): UserDetailsChangedEvent {
        val changeEvent = UserDetailsChangedEvent()
        val permissionService = context.getBean(PermissionService::class.java)

        if (update.type == null && (update.permissions != null || update.roles != null)) {
            throw UserDetailsChangeException("No update type provided but changes were provided")
        }

        if (update.permissions != null) {
            if (!Permission.areValidPermissions(update.permissions!!)) throw PermissionsInvalidException()
            permissionService.modifyPermissions(targetId, update.permissions!!, update.type!!)
        }

        if (update.roles != null) {
            val roles = update.roles!!.map { ObjectIdHelper.getId(it) }.toSet()
            permissionService.modifyRoles(targetId, roles, update.type!!)
            changeEvent.roles = permissionService.getUserRoles(targetId).map { RoleDto(it) }
        }
        changeEvent.permissions = permissionService.getUserPermissions(targetId)

        if (update.settings != null) {
            val userSettingsService = context.getBean(UserSettingsService::class.java)
            val update = userSettingsService.update(targetId, update.settings!!)
            changeEvent.settings = UserSettingsDto(update)
        }
        return changeEvent
    }
}