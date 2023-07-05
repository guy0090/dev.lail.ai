package ai.lail.api.exceptions.websocket

import ai.lail.api.exceptions.AbstractException
import ai.lail.api.exceptions.ExceptionCode
import ai.lail.api.websocket.WebSocketCommand

class DuplicateAssociationIdException(message: String?, cause: Throwable?) : AbstractException() {
    override val message: String = message ?: "Duplicate association ID"
    override val status: Int = 400
    override val cause: Throwable = cause ?: Throwable(message)
    override val code = ExceptionCode.DUPLICATE_ASSOCIATION_ID

    constructor() : this(null, null)
    constructor(message: String?) : this(message, null)
    constructor(cause: Throwable?) : this(null, cause)
    constructor(command: WebSocketCommand) : this("Duplicate association ID: ${command.assoc}", null)
}