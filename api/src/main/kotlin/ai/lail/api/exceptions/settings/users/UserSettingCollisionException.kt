package ai.lail.api.exceptions.settings.users

import ai.lail.api.exceptions.AbstractException
import ai.lail.api.exceptions.ExceptionCode

class UserSettingCollisionException(message: String?, cause: Throwable?) : AbstractException() {
    override val message: String = message ?: "Settings collision"
    override val status: Int = 400
    override val cause: Throwable = cause ?: Throwable(message)
    override val code = ExceptionCode.USER_SETTINGS_COLLISION

    constructor() : this(null, null)
    constructor(message: String) : this(message, null)
    constructor(ex: Exception) : this(ex.message, ex.cause)
}