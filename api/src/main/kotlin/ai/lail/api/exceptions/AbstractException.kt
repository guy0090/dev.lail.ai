package ai.lail.api.exceptions

/**
 * Abstract exception class for all custom exceptions
 */
abstract class AbstractException(
    override val message: String? = "An error occurred.",
    override val cause: Throwable? = Throwable(message),
    open val status: Int? = 400,
    open val code: ExceptionCode = ExceptionCode.COMMON,
) : Exception() {
    constructor(message: String) : this(message, null)
    constructor(ex: Exception) : this(ex.message, ex.cause, null)
}