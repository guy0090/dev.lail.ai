package ai.lail.api.exceptions.encounters

import ai.lail.api.exceptions.AbstractException
import ai.lail.api.exceptions.ExceptionCode
import org.springframework.http.HttpStatus

class EncounterUploadLimitException(message: String?, cause: Throwable?) : AbstractException() {
    override val message: String = message ?: "Cannot upload more encounters"
    override val status: Int = HttpStatus.BAD_REQUEST.value()
    override val cause: Throwable = cause ?: Throwable(message)
    override val code = ExceptionCode.ENCOUNTER_UPLOAD_LIMIT

    constructor() : this(null, null)
    constructor(message: String) : this(message, null)
    constructor(count: Int) : this("Cannot upload more than $count encounters", null)
    constructor(ex: Exception) : this(ex.message, ex.cause)
}