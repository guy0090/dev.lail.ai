package ai.lail.api.exceptions.guilds

import ai.lail.api.exceptions.AbstractException
import ai.lail.api.exceptions.ExceptionCode

class GuildRoleRemoveException(message: String?, cause: Throwable?) : AbstractException() {
    override val message: String = message ?: "Guild role cannot be removed."
    override val status: Int = 400
    override val cause: Throwable = cause ?: Throwable(message)
    override val code = ExceptionCode.GUILD_ROLE_REMOVE

    constructor(message: String) : this(message, null)
    constructor(ex: Exception) : this(ex.message, ex.cause)
}