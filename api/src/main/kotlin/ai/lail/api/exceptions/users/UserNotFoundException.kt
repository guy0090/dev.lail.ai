package ai.lail.api.exceptions.users

import ai.lail.api.exceptions.AbstractException
import ai.lail.api.exceptions.ExceptionCode
import org.bson.types.ObjectId
import org.springframework.http.HttpStatus

class UserNotFoundException(message: String?, cause: Throwable?) : AbstractException() {
    override val message: String = message ?: "User not found"
    override val status: Int = HttpStatus.BAD_REQUEST.value()
    override val cause: Throwable = cause ?: Throwable(message)
    override val code = ExceptionCode.USER_NOT_FOUND

    constructor() : this(null, null)
    constructor(message: String) : this(message, null)
    constructor(userId: ObjectId) : this("User with ID ${userId.toHexString()} not found.", null)
    constructor(ex: Exception) : this(ex.message, ex.cause)
}
