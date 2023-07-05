package ai.lail.api.exceptions.permissions

import ai.lail.api.exceptions.AbstractException
import ai.lail.api.exceptions.ExceptionCode
import org.springframework.http.HttpStatus

class PermissionsInvalidException(message: String?, cause: Throwable?) : AbstractException() {
    override val message: String = message ?: "Permissions are invalid"
    override val status: Int = HttpStatus.BAD_REQUEST.value()
    override val cause: Throwable = cause ?: Throwable(message)
    override val code = ExceptionCode.PERMISSION_INVALID

    constructor() : this(null, null)
}