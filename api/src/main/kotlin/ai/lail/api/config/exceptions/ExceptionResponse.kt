package ai.lail.api.config.exceptions

import ai.lail.api.exceptions.ExceptionCode
import java.io.Serializable
import java.time.Instant
import java.util.*

data class ExceptionResponse(
    val status: Int,
    val message: String,
    val path: String,
    val timestamp: String? = Instant.now().toString(),
    val id: UUID? = UUID.randomUUID(),
    val code: Int? = ExceptionCode.COMMON.value
) : Serializable