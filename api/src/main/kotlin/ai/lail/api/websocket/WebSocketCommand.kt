package ai.lail.api.websocket

import ai.lail.api.data.users.User
import ai.lail.api.exceptions.websocket.InvalidWsContentException
import ai.lail.api.util.ObjectIdHelper
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import org.bson.types.ObjectId
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import java.util.*

/**
 * The base class for all commands sent from the client to the server.
 *
 * Commands are deserialized using a custom [CommandDeserializer]. This is necessary because the
 * [WebSocketEvent] enum requires a permission and potential return value in addition to the event ID
 * to initialize, which the client does not provide.
 *
 * Any commands that extend this one may introduce their own parameters by attempting to deserialize
 * them out of [content]. If the command does not have any parameters, [content] will be null.
 *
 * @property event The event that triggered this command
 * @property content The content of the command
 * @property assoc The UUID of the command used to associate the command with its [WebSocketResponse]
 * @property sender The user that sent the command (if any)
 *
 * @see CommandDeserializer
 * @see WebSocketResponse
 * @see WebSocketEvent
 */
@JsonDeserialize(using = CommandDeserializer::class)
abstract class WebSocketCommand(command: CommandContent) {
    val logger: Logger = LoggerFactory.getLogger(this::class.java)
    abstract val event: WebSocketEvent
    val content: JsonNode = command.content
    val assoc: UUID = command.assoc
    var sender: User? = null

    /**
     * Responsible for handling the result of the response and failing or completing the
     * passed [WebSocketResponse] property.
     *
     * In order to use beans (services, components, etc), [ApplicationContext] is also passed to allow access
     * via [ApplicationContext.getBean]
     *
     * @param response The response to handle
     * @param context The Spring application context for Bean access
     */
    abstract fun handle(response: WebSocketResponse, context: ApplicationContext)


    protected fun getObjectId(key: String): ObjectId {
        val id = content.get(key) ?: throw InvalidWsContentException("Missing $key")
        return ObjectIdHelper.getId(id.asText())
    }


    protected fun getObjectIds(key: String, min: Int? = 1, max: Int? = 50): Set<ObjectId> {
        val list = content.get(key)?.asIterable() ?: throw InvalidWsContentException("Missing $key")
        if (min != null && list.count() < min) throw InvalidWsContentException("Too few IDs provided (min: $min)")
        if (max != null && list.count() > max) throw InvalidWsContentException("Too many IDs provided (max: $max)")
        return list.map { ObjectIdHelper.getId(it.asText()) }.toSet()
    }


    companion object {
        /**
         * Returns the command that matches the passed [WebSocketEvent] via reflection.
         *
         * *All* commands must be in the `ai.lail.api.websocket.commands` package and must be named
         * `*Command` where `*` is the name of the event found in [WebSocketEvent].
         *
         * @param event The event to get the command for
         * @param content The content of the command
         */
        fun getCommand(event: WebSocketEvent, content: CommandContent): WebSocketCommand {
            val clazz = Class.forName("ai.lail.api.websocket.commands.${event.name}Command").kotlin
            return clazz.constructors.first().call(content) as WebSocketCommand
        }
    }
}