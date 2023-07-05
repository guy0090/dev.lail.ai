package ai.lail.api.exceptions.guilds

import ai.lail.api.exceptions.AbstractException
import ai.lail.api.exceptions.ExceptionCode
import org.springframework.http.HttpStatus

class GuildInvalidPermissionException(message: String?, cause: Throwable?) : AbstractException() {
    override val message: String = message ?: "Invalid guild permission."
    override val status: Int = HttpStatus.BAD_REQUEST.value()
    override val cause: Throwable = cause ?: Throwable(message)
    override val code = ExceptionCode.GUILD_INVALID_PERMISSIONS

    constructor() : this(null, null)
    constructor(ex: Exception) : this(ex.message, ex)
    constructor(permission: String) : this("Invalid guild permission: $permission", null)
    constructor(permissions: List<String>) : this("Invalid guild permissions: ${permissions.joinToString(",")}", null)
}
