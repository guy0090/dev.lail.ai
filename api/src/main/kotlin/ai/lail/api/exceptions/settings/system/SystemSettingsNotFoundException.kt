package ai.lail.api.exceptions.settings.system

import ai.lail.api.exceptions.AbstractException
import ai.lail.api.exceptions.ExceptionCode

class SystemSettingsNotFoundException(message: String?, cause: Throwable?) : AbstractException() {
    override val message: String = message ?: "System settings not found"
    override val status: Int = 404
    override val cause: Throwable = cause ?: Throwable(message)
    override val code = ExceptionCode.SYSTEM_SETTINGS_NOT_FOUND

    constructor() : this(null, null)
}