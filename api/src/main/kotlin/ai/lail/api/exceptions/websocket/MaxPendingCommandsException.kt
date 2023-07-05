package ai.lail.api.exceptions.websocket

import ai.lail.api.exceptions.AbstractException
import ai.lail.api.exceptions.ExceptionCode

class MaxPendingCommandsException(message: String?, cause: Throwable?) : AbstractException() {
    override val message: String = message ?: "Maximum number of pending commands reached"
    override val status: Int = 400
    override val cause: Throwable = cause ?: Throwable(message)
    override val code = ExceptionCode.MAX_PENDING_COMMANDS

    constructor(message: String?) : this(message, null)
    constructor(cause: Throwable?) : this(null, cause)
    constructor() : this(null, null)
}