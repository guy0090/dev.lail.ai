package ai.lail.api.exceptions.keys

import ai.lail.api.exceptions.AbstractException
import ai.lail.api.exceptions.ExceptionCode
import org.springframework.http.HttpStatus

class NoKeyPermissionsException(message: String?, cause: Throwable?) : AbstractException() {
    override val message: String = message ?: "Key has no permissions"
    override val status: Int = HttpStatus.BAD_REQUEST.value()
    override val cause: Throwable = cause ?: Throwable(message)
    override val code = ExceptionCode.KEY_NO_PERMISSIONS

    constructor() : this(null, null)
    constructor(message: String) : this(message, null)
    constructor(ex: Exception) : this(ex.message, ex.cause)
}