package ai.lail.api.exceptions.common

import ai.lail.api.exceptions.AbstractException
import ai.lail.api.exceptions.ExceptionCode
import org.springframework.http.HttpStatus

class SaveDataException(message: String?, cause: Throwable?) : AbstractException() {
    override val message: String = message ?: "Saving data failed"
    override val status: Int = HttpStatus.INTERNAL_SERVER_ERROR.value()
    override val cause: Throwable = cause ?: Throwable(message)
    override val code = ExceptionCode.SAVE_DATA

    constructor() : this(null, null)
}