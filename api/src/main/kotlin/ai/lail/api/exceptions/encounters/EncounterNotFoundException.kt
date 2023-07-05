package ai.lail.api.exceptions.encounters

import ai.lail.api.exceptions.AbstractException
import ai.lail.api.exceptions.ExceptionCode
import org.springframework.http.HttpStatus

class EncounterNotFoundException(message: String?, cause: Throwable?) : AbstractException() {
    override val message: String = message ?: "Encounter not found"
    override val status: Int = HttpStatus.NOT_FOUND.value()
    override val cause: Throwable = cause ?: Throwable(message)
    override val code = ExceptionCode.ENCOUNTER_NOT_FOUND

    constructor() : this(null, null)
    constructor(message: String) : this(message, null)
    constructor(ex: Exception) : this(ex.message, ex.cause)
}