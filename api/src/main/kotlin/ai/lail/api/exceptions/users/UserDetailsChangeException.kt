package ai.lail.api.exceptions.users

import ai.lail.api.exceptions.AbstractException
import ai.lail.api.exceptions.ExceptionCode

class UserDetailsChangeException(message: String?, cause: Throwable?) : AbstractException() {
    override val message: String = message ?: "Invalid user details change"
    override val status: Int = 400
    override val cause: Throwable = cause ?: Throwable(message)
    override val code = ExceptionCode.USER_DETAILS_CHANGE_FAILED

    constructor(message: String?) : this(message, null)
}