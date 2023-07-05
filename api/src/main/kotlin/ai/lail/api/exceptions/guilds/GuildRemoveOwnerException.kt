package ai.lail.api.exceptions.guilds

import ai.lail.api.exceptions.AbstractException
import ai.lail.api.exceptions.ExceptionCode
import org.springframework.http.HttpStatus

class GuildRemoveOwnerException(message: String?, cause: Throwable?) : AbstractException() {
    override val message: String = message ?: "Cannot remove the owner of a guild."
    override val status: Int = HttpStatus.BAD_REQUEST.value()
    override val cause: Throwable = cause ?: Throwable(message)
    override val code = ExceptionCode.GUILD_REMOVE_OWNER

    constructor() : this(null, null)
    constructor(message: String) : this(message, null)
    constructor(ex: Exception) : this(ex.message, ex)
}