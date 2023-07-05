package ai.lail.api.exceptions.guilds

import ai.lail.api.exceptions.AbstractException
import ai.lail.api.exceptions.ExceptionCode
import org.bson.types.ObjectId
import org.springframework.http.HttpStatus

class GuildNotFoundException(message: String?, cause: Throwable?) : AbstractException() {
    override val message: String = message ?: "Guild not found."
    override val status: Int = HttpStatus.BAD_REQUEST.value()
    override val cause: Throwable = cause ?: Throwable(message)
    override val code = ExceptionCode.GUILD_NOT_FOUND

    constructor() : this(null, null)
    constructor(ex: Exception) : this(ex.message, ex.cause)
    constructor(guildId: String) : this("Guild with ID $guildId not found.", null)
    constructor(guildId: ObjectId) : this("Guild with ID ${guildId.toHexString()} not found.", null)
}