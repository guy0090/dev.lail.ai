package ai.lail.api.exceptions.users

import ai.lail.api.exceptions.AbstractException
import ai.lail.api.exceptions.ExceptionCode
import org.bson.types.ObjectId

class UserAlreadyInGuildException(message: String?, cause: Throwable?) : AbstractException() {
    override val message: String = message ?: "User already in guild"
    override val status: Int = 400
    override val cause: Throwable = cause ?: Throwable(message)
    override val code = ExceptionCode.USER_ALREADY_IN_GUILD

    constructor() : this(null, null)
    constructor(userId: ObjectId) : this("User $userId already in guild", null)
    constructor(message: String) : this(message, null)
    constructor(ex: Exception) : this(ex.message, ex.cause)
}