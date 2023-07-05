package ai.lail.api.exceptions.guilds

import ai.lail.api.exceptions.AbstractException
import ai.lail.api.exceptions.ExceptionCode
import org.bson.types.ObjectId
import org.springframework.http.HttpStatus

class GuildMemberNotFoundException(message: String?, cause: Throwable?) : AbstractException() {
    override val message: String = message ?: "Guild member not found."
    override val status: Int = HttpStatus.BAD_REQUEST.value()
    override val cause: Throwable = cause ?: Throwable(message)
    override val code = ExceptionCode.GUILD_MEMBER_NOT_FOUND

    constructor() : this(null, null)
    constructor(message: String) : this(message, null)
    constructor(ex: Exception) : this(ex.message, ex.cause)
    constructor(userId: ObjectId) : this("Guild member with ID ${userId.toHexString()} not found.", null)
}