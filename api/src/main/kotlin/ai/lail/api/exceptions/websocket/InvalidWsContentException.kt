package ai.lail.api.exceptions.websocket

import ai.lail.api.exceptions.AbstractException
import ai.lail.api.exceptions.ExceptionCode

class InvalidWsContentException(message: String?, cause: Throwable?) : AbstractException() {
    override val message: String = message ?: "Invalid WebSocket content"
    override val status: Int = 400
    override val cause: Throwable = cause ?: Throwable(message)
    override val code = ExceptionCode.INVALID_WS_CONTENT

    constructor() : this(null, null)
    constructor(message: String) : this(message, null)
    constructor(ex: Exception) : this(ex.message, ex.cause)
}