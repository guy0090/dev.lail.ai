package ai.lail.api.exceptions.permissions

import ai.lail.api.exceptions.AbstractException
import ai.lail.api.exceptions.ExceptionCode
import org.springframework.http.HttpStatus

class RemoveDeletedRoleFromUserException(message: String?, cause: Throwable?) : AbstractException() {
    override val message: String = message ?: "Failed to remove deleted role from user"
    override val status: Int = HttpStatus.BAD_REQUEST.value()
    override val cause: Throwable = cause ?: Throwable(message)
    override val code = ExceptionCode.REMOVE_DELETED_USER_ROLE_FAILED

    constructor() : this(null, null)
    constructor(message: String) : this(message, null)
    constructor(ex: Exception) : this(ex.message, ex.cause)
}