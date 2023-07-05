package ai.lail.api.websocket

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.JsonNode
import java.io.Serializable
import java.util.*

/**
 * A response to a command.
 *
 * @param event The event that this response is for.
 * @param assoc The association ID of the command that this response is for.
 * @param created The time that this response was created.
 * @param data The data that this response contains, if any.
 * @param error The error that this response contains, if any.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class WebSocketResponse(
    @JsonIgnore
    val event: WebSocketEvent,
    val assoc: UUID = UUID.randomUUID(),
    val created: Long = Date().time,
    var data: JsonNode? = null,
    var error: JsonNode? = null
) : Serializable {
    fun isExpired(max: Int): Boolean {
        val now = Date()
        val diff = now.time - created
        return diff > max
    }

    fun complete(result: JsonNode) {
        if (this.error != null)
            throw IllegalStateException("Cannot complete a response that has already failed.")
        this.data = result
    }

    fun fail(error: JsonNode) {
        if (this.data != null)
            throw IllegalStateException("Cannot fail a response that has already been completed.")
        this.error = error
    }
}