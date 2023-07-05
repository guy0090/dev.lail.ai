package ai.lail.api.exceptions.keys

import ai.lail.api.exceptions.AbstractException
import ai.lail.api.exceptions.ExceptionCode
import org.bson.types.ObjectId
import org.springframework.http.HttpStatus

class KeyNotFoundException(message: String?, cause: Throwable?) : AbstractException() {
    override val message: String = message ?: "Key not found"
    override val status: Int = HttpStatus.BAD_REQUEST.value()
    override val cause: Throwable = cause ?: Throwable(message)
    override val code = ExceptionCode.KEY_NOT_FOUND

    constructor() : this(null, null)
    constructor(keyId: ObjectId) : this("Key with ID $keyId not found", null)
    constructor(message: String) : this(message, null)
    constructor(ex: Exception) : this(ex.message, ex.cause)
}