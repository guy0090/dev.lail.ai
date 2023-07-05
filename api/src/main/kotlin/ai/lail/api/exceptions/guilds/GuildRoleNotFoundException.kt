package ai.lail.api.exceptions.guilds

import ai.lail.api.exceptions.AbstractException
import ai.lail.api.exceptions.ExceptionCode
import org.bson.types.ObjectId
import org.springframework.http.HttpStatus

class GuildRoleNotFoundException(message: String?, cause: Throwable?) : AbstractException() {
    override val message: String = "Guild role not found."
    override val status: Int = HttpStatus.BAD_REQUEST.value()
    override val cause: Throwable = cause ?: Throwable(message)
    override val code = ExceptionCode.GUILD_ROLE_NOT_FOUND

    constructor() : this(null, null)
    constructor(message: String) : this(message, null)
    constructor(ex: Exception) : this(ex.message, ex.cause)
    constructor(
        guildId: ObjectId,
        roleIds: List<ObjectId>
    ) : this(
        "Guild roles with IDs ${roleIds.joinToString(",") { it.toHexString() }} not found in guild with ID ${guildId.toHexString()}.",
        null
    )

    constructor(
        guildId: ObjectId,
        roleId: ObjectId
    ) : this("Guild role with ID ${roleId.toHexString()} not found in guild with ID ${guildId.toHexString()}.", null)
}