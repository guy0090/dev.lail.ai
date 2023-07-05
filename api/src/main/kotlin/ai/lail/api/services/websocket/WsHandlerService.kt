package ai.lail.api.services.websocket

import ai.lail.api.data.users.User
import ai.lail.api.exceptions.websocket.DuplicateAssociationIdException
import ai.lail.api.exceptions.websocket.MaxPendingCommandsException
import ai.lail.api.services.PermissionService
import ai.lail.api.websocket.WebSocketCommand
import ai.lail.api.websocket.WebSocketEvent
import ai.lail.api.websocket.WebSocketResponse
import ai.lail.api.websocket.WsConstants.MAX_PENDING
import ai.lail.api.websocket.WsConstants.PENDING_TTL_MS
import ai.lail.api.websocket.commands.*
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.web.socket.WebSocketSession
import java.util.*
import java.util.concurrent.TimeUnit


@Service
class WsHandlerService(
    val permissionService: PermissionService,
    val applicationContext: ApplicationContext
) {
    val logger: Logger = LoggerFactory.getLogger(WsHandlerService::class.java)

    /**
     * Map of pending responses, indexed by the UUID of the command that triggered them.
     * - The [UUID] *must* be transmitted as the field [WebSocketCommand.assoc] in the command.
     *      - The UUID is generated by the client and is used to identify the response.
     *      - This is not secure, as the client can generate any UUID it wants.
     *        Checks & validation should deter malicious clients, but this is not a foolproof solution.
     * - The response is removed from the map when the command is handled or when the scheduled task [checkPending]
     *  runs and finds that the response has expired. Default TTL is defined as [PENDING_TTL_MS].
     *
     * Additionally, the map is limited to [MAX_PENDING] entries. If the map is full
     * when a new command is received, any attempts to add a new entry will fail.
     *
     * @see createPending
     * @see removePending
     */
    val pending = mutableMapOf<UUID, WebSocketResponse>()


    /**
     * Handles creation and validation of a [WebSocketResponse].
     *
     * Does not allow the creation of a response if the associated ID is already present in the [pending] map or
     * if the [pending] map is full ([MAX_PENDING]).
     *
     * @param command The command to create a pending response for
     * @return The pending response
     *
     * @throws Exception If the ID is already present or there are too many pending responses
     *
     * @see pending
     */
    @Throws(DuplicateAssociationIdException::class, MaxPendingCommandsException::class)
    fun createPending(command: WebSocketCommand): WebSocketResponse {
        if (pending.containsKey(command.assoc)) throw DuplicateAssociationIdException(command)
        if (pending.size >= MAX_PENDING) throw MaxPendingCommandsException()

        val response = WebSocketResponse(command.event, command.assoc)
        pending[command.assoc] = response
        return response
    }


    /**
     * Remove a pending response from the [pending] map.
     *
     * @param assoc The association ID of the pending response to remove
     * @return The removed pending response, or null if it was not found
     */
    fun removePending(assoc: UUID): WebSocketResponse? = pending.remove(assoc)


    /**
     * Check for expired pending [WebSocketResponse]s at a fixed rate.
     *
     * Expired responses are removed from the [pending] map.
     */
    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.SECONDS)
    private fun checkPending() {
        for ((key, value) in pending) {
            if (value.isExpired(PENDING_TTL_MS)) {
                logger.info("Pending response expired: $key")
                removePending(key)
            }
        }
    }


    /**
     * Utility method to create an error [WebSocketResponse].
     *
     * @param assoc The association ID of the command that failed to be handled
     * @param reason The reason the command failed to be handled
     * @return The error response
     */
    fun createErrorResponse(assoc: UUID, reason: String): WebSocketResponse {
        val response = WebSocketResponse(WebSocketEvent.Error, assoc)
        val content = ObjectMapper().createObjectNode()
        content.put("message", reason)
        response.fail(content)
        return response
    }


    /**
     * Handle a WebSocket command.
     *
     * The process is as follows:
     * - If the command requires a permission, the [WebSocketCommand.sender] field is checked to ensure it is not null.
     * This is done because the [WebSocketCommand.sender] field is previously set to the [User] object associated with
     * the WebSocket session, which may be null if the [WebSocketSession] does not have a "user" attribute. Should the
     * subject be null, but the command requires a permission, the command is not handled and a custom error response is
     * returned.
     *
     * - If the previous step was successful, the command is handled by the [WebSocketCommand.handle] method found on every command class.
     *
     * @param command The command to handle
     * @return The response to send to the client
     *
     * @see createErrorResponse
     */
    fun handleCommand(command: WebSocketCommand): WebSocketResponse {
        if (command.event.requiresPermission()) {
            if (command.sender == null) return createErrorResponse(command.assoc, "Subject is null")
            else if (!permissionService.hasPermission(
                    command.sender!!.id,
                    command.event.permission
                )
            ) return createErrorResponse(command.assoc, "Permission denied")
        }

        var response: WebSocketResponse
        try {
            response = createPending(command)
            command.handle(response, applicationContext)
        } catch (e: Exception) {
            response = createErrorResponse(command.assoc, e.message ?: "An unknown error occurred")
        }
        return response
    }

}