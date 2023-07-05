package ai.lail.api.websocket

import ai.lail.api.exceptions.websocket.InvalidWsContentException
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import java.io.IOException
import java.util.*

class CommandDeserializer : StdDeserializer<WebSocketCommand> {
    constructor() : this(null)

    constructor(vc: Class<*>?) : super(vc)

    @Throws(IOException::class, JsonProcessingException::class, InvalidWsContentException::class)
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): WebSocketCommand {
        val node = p.codec.readTree<JsonNode>(p)
        val event = WebSocketEvent.fromId(node.get("event").asInt())
            ?: throw InvalidWsContentException("Missing event identifier")
        val content = node.get("content")
            ?: throw InvalidWsContentException("Missing content")
        val assoc = node.get("assoc")?.asText()?.let { UUID.fromString(it) }
            ?: throw InvalidWsContentException("Missing association identifier")

        val commandContent = CommandContent(content, assoc)
        return WebSocketCommand.getCommand(event, commandContent)
    }
}
