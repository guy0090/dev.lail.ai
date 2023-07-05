package ai.lail.api.exceptions.followers

import ai.lail.api.exceptions.AbstractException
import ai.lail.api.exceptions.ExceptionCode
import org.springframework.http.HttpStatus

class FollowersUpdateException(message: String?, cause: Throwable?) : AbstractException() {
    override val message: String = message ?: "Failed to update followers"
    override val status: Int = HttpStatus.BAD_REQUEST.value()
    override val cause: Throwable = cause ?: Throwable(message)
    override val code = ExceptionCode.FOLLOWERS_UPDATE_FAILED

    constructor() : this(null, null)
    constructor(message: String) : this(message, null)
    constructor(ex: Exception) : this(ex.message, ex.cause)
}