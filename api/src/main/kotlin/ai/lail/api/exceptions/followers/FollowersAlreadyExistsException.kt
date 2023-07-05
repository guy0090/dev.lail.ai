package ai.lail.api.exceptions.followers

import ai.lail.api.exceptions.AbstractException
import ai.lail.api.exceptions.ExceptionCode
import org.springframework.http.HttpStatus

class FollowersAlreadyExistsException(message: String?, cause: Throwable?) : AbstractException() {
    override val message: String = message ?: "Followers object already exists"
    override val status: Int = HttpStatus.BAD_REQUEST.value()
    override val cause: Throwable = cause ?: Throwable(message)
    override val code = ExceptionCode.FOLLOWERS_ALREADY_EXISTS

    constructor() : this(null, null)
    constructor(message: String) : this(message, null)
    constructor(ex: Exception) : this(ex.message, ex.cause)
}