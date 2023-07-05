package ai.lail.api.exceptions.common

import ai.lail.api.exceptions.AbstractException
import ai.lail.api.exceptions.ExceptionCode

class InvalidObjectIdException(message: String?, cause: Throwable?) : AbstractException() {
    override val message: String = message ?: "Invalid ObjectId"
    override val status: Int = 400
    override val cause: Throwable = cause ?: Throwable(message)
    override val code = ExceptionCode.INVALID_OBJECT_ID

    constructor() : this(null, null)
    constructor(message: String) : this(message, null)
    constructor(ex: Exception) : this(ex.message, ex)
}