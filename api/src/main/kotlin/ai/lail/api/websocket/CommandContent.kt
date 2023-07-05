package ai.lail.api.websocket

import com.fasterxml.jackson.databind.JsonNode
import java.util.*

/**
 * Value object for the content of a [WebSocketCommand].
 */
data class CommandContent(val content: JsonNode, val assoc: UUID)