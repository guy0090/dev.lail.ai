package ai.lail.api.exceptions.common

import ai.lail.api.exceptions.AbstractException
import ai.lail.api.exceptions.ExceptionCode
import org.springframework.http.HttpStatus

class SignUpDisabledException(message: String?, cause: Throwable?) : AbstractException() {
    override val message: String = message ?: "User registration is disabled"
    override val status: Int = HttpStatus.BAD_REQUEST.value()
    override val cause: Throwable = cause ?: Throwable(message)
    override val code = ExceptionCode.SIGN_UPS_DISABLED

    constructor() : this(null, null)
}