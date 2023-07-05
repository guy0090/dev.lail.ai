package ai.lail.api.controllers.websocket

import ai.lail.api.config.services.ProjectConfigurationService
import ai.lail.api.data.users.User
import ai.lail.api.exceptions.websocket.InvalidCommandException
import ai.lail.api.services.AdminService
import ai.lail.api.services.EncounterService
import ai.lail.api.services.websocket.WsHandlerService
import ai.lail.api.services.websocket.WsPublishService
import ai.lail.api.services.websocket.WsSessionService
import ai.lail.api.websocket.WebSocketCommand
import ai.lail.api.websocket.events.WelcomeEvent
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

@Controller
class WebSocketController(
    val wsSessionService: WsSessionService,
    val wsPublishService: WsPublishService,
    val adminService: AdminService,
    val projectService: ProjectConfigurationService,
    val encounterService: EncounterService,
    val commandHandlerService: WsHandlerService
) : TextWebSocketHandler() {
    val logger: Logger = LoggerFactory.getLogger(WebSocketController::class.java)


    /**
     * Handle all incoming WebSocket commands.
     *
     * The incoming command is initially checked for validity (length). If the validity is confirmed,
     * the command is parsed.
     *
     *  - Parsing the command is done by [parseCommand]. The process of parsing is done by attempting to
     *    associate the incoming JSON string with a [WebSocketCommand] class. If the command is not
     *    recognized, the message is ignored and no response is sent.
     *
     * See [parseCommand] for a more detailed explanation of the parsing process.
     *
     * - If the command is recognized, it is handled by [WsHandlerService.handleCommand].
     *
     * See [WsHandlerService.handleCommand] for a more detailed explanation of the handling process.
     *
     * Should any step fail unexpectedly, the connection is closed with a status code of [CloseStatus.SERVER_ERROR]
     * and the error is logged.
     *
     * @param session The WebSocket session that sent the command
     * @param message The incoming message
     *
     * @see parseCommand
     * @see WsHandlerService.handleCommand
     */
    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        // If the message is empty, close the connection
        if (message.payloadLength == 0) {
            if (session.isOpen) session.close(CloseStatus(CloseStatus.POLICY_VIOLATION.code, "Empty message"))
            return
        }

        try {
            // Attempt to parse the command
            val command = parseCommand(message.payload, session) ?: throw InvalidCommandException()
            // If the command was successfully parsed, handle it
            val response = commandHandlerService.handleCommand(command)
            // Send the response
            wsPublishService.sendResponse(session, response)
            // Remove the pending response
            commandHandlerService.removePending(command.assoc)
        } catch (e: Exception) {
            // If an unexpected error occurs, close the connection
            logger.error("Error while handling WebSocket message: ${e.message}")
            // If an unexpected error occurs, close the connection
            var status = CloseStatus.SERVER_ERROR
            var error = "Internal server error"
            if (e is InvalidCommandException) {
                status = CloseStatus.POLICY_VIOLATION
                error = "Invalid command"
            }
            if (session.isOpen) session.close(CloseStatus(status.code, error))
        }
    }


    /**
     * Handle the initial connection of a WebSocket session.
     *
     * The session is saved to the [WsSessionService] and a welcome message is sent.
     *
     * @param session The WebSocket session that was opened
     *
     * @see WsSessionService.saveSession
     * @see sendWelcomeMessage
     */
    override fun afterConnectionEstablished(session: WebSocketSession) {
        wsSessionService.saveSession(session)
        sendWelcomeMessage(session)
    }


    /**
     * Handle the closing of a WebSocket session.
     *
     * The session is removed from the [WsSessionService].
     *
     * @param session The WebSocket session that was closed
     * @param status The status of the closed session
     *
     * @see WsSessionService.deleteWebSocketSession
     */
    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        wsSessionService.deleteWebSocketSession(session.id)
    }


    /**
     * Parse a WebSocket command from an incoming [TextMessage] JSON string.
     *
     * The process is as follows:
     * 1. The provided payload string is parsed into a [WebSocketCommand] object via [ObjectMapper.readValue]. If
     * the payload is not valid JSON or not a supported event, an error will be thrown and null returned.
     *
     * 2. The [WebSocketCommand.sender] field is set to the [User] object associated with the WebSocket session.
     * This may be null if the [WebSocketSession] does not have a "user" attribute.
     *
     * 3. If the command is recognized it is returned, otherwise null is returned
     *
     * For a detailed explanation of the command classes, see [WebSocketCommand].
     *
     * @param payload The JSON string to parse
     * @param session The WebSocket session that sent the command
     * @return The parsed command, or null if the command could not be parsed
     *
     * @see WebSocketCommand
     */
    private fun parseCommand(payload: String, session: WebSocketSession): WebSocketCommand? {
        return try {
            val command = ObjectMapper().readValue(payload, WebSocketCommand::class.java)
            command.sender = session.attributes["user"] as? User
            return command
        } catch (e: Exception) {
            logger.error("Error while parsing WebSocket message: ${e.message}", e)
            null
        }
    }


    private fun sendWelcomeMessage(session: WebSocketSession) {
        val settings = adminService.getSettings()
        val event = WelcomeEvent(
            projectService.getVersion(),
            encounterService.count(),
            settings?.initDone ?: false
        )
        wsPublishService.sendMessage(session, event)
    }

}