package ai.lail.api.exceptions.encounters

import ai.lail.api.exceptions.AbstractException
import ai.lail.api.exceptions.ExceptionCode
import org.springframework.http.HttpStatus

class EncounterCompressionException(message: String?, cause: Throwable?) : AbstractException() {
    override val message: String = message ?: "Encounter could not be compressed"
    override val status: Int = HttpStatus.BAD_REQUEST.value()
    override val cause: Throwable = cause ?: Throwable(message)
    override val code = ExceptionCode.ENCOUNTER_COMPRESSION_FAILED
    constructor(ex: Exception) : this(ex.message, ex.cause)
}