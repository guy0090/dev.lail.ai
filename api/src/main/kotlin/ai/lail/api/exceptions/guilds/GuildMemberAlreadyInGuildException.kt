package ai.lail.api.exceptions.guilds

import ai.lail.api.exceptions.AbstractException
import ai.lail.api.exceptions.ExceptionCode
import org.bson.types.ObjectId
import org.springframework.http.HttpStatus

class GuildMemberAlreadyInGuildException(message: String?, cause: Throwable?) : AbstractException() {
    override val message: String = message ?: "Guild member already exists."
    override val status: Int = HttpStatus.BAD_REQUEST.value()
    override val cause: Throwable = cause ?: Throwable(message)
    override val code = ExceptionCode.GUILD_MEMBER_ALREADY_IN_GUILD

    constructor() : this(null, null)
    constructor(message: String) : this(message, null)
    constructor(message: Exception) : this(message.message, message.cause)
    constructor(
        guildId: ObjectId,
        userId: ObjectId
    ) : this(
        "Guild member with ID ${userId.toHexString()} already exists in guild with ID ${guildId.toHexString()}.",
        null
    )
}