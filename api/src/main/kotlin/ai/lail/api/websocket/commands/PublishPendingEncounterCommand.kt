package ai.lail.api.websocket.commands

import ai.lail.api.websocket.CommandContent
import ai.lail.api.websocket.WebSocketCommand
import ai.lail.api.websocket.WebSocketEvent
import ai.lail.api.websocket.WebSocketResponse
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.ApplicationContext

class PublishPendingEncounterCommand(body: CommandContent) : WebSocketCommand(body) {
    override val event: WebSocketEvent = WebSocketEvent.PublishPendingEncounter
    private val association: String = content.get("association").asText()

    override fun handle(response: WebSocketResponse, context: ApplicationContext) {
        val content = ObjectMapper().createObjectNode()
        try {
            content.put("message", "Publishing pending encounter")
            logger.info("Publishing pending encounter: $association")
            // TODO
            response.complete(content)
        } catch (e: Exception) {
            content.put("message", "An unknown error occurred")
            response.fail(content)
        }
    }
}