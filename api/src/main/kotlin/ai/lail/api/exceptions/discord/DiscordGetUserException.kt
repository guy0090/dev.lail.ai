package ai.lail.api.exceptions.discord

import ai.lail.api.exceptions.AbstractException
import ai.lail.api.exceptions.ExceptionCode

class DiscordGetUserException(message: String?, cause: Throwable?) : AbstractException() {
    override val message: String = message ?: "Discord user could not be retrieved."
    override val cause: Throwable = cause ?: Throwable(message)
    override val status: Int = 400
    override val code = ExceptionCode.DISCORD_GET_USER

    constructor(message: String) : this(message, null)
    constructor(ex: Exception) : this(ex.message, ex.cause)
}