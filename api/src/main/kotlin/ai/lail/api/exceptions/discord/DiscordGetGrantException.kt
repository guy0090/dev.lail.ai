package ai.lail.api.exceptions.discord

import ai.lail.api.exceptions.AbstractException
import ai.lail.api.exceptions.ExceptionCode

class DiscordGetGrantException(message: String?, cause: Throwable?) : AbstractException() {
    override val message: String = message ?: "Discord OAuth grant could not be retrieved."
    override val cause: Throwable = cause ?: Throwable(message)
    override val status: Int = 400
    override val code = ExceptionCode.DISCORD_GET_GRANT

    constructor(ex: Exception) : this(ex.message, ex.cause)
}