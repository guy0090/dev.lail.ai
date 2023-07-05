package ai.lail.api.exceptions.roles

import ai.lail.api.exceptions.AbstractException
import ai.lail.api.exceptions.ExceptionCode
import org.springframework.http.HttpStatus

class RolesDeletePermanentException(message: String?, cause: Throwable?) : AbstractException() {
    override val message: String = message ?: "Cannot delete permanent role"
    override val status: Int = HttpStatus.BAD_REQUEST.value()
    override val cause: Throwable = cause ?: Throwable(message)
    override val code = ExceptionCode.ROLES_DELETE_PERMANENT_ROLE

    constructor() : this(null, null)
    constructor(message: String) : this(message, null)
    constructor(ex: Exception) : this(ex.message, ex.cause)
}