package ai.lail.api.exceptions.settings.users

import ai.lail.api.exceptions.AbstractException
import ai.lail.api.exceptions.ExceptionCode
import org.bson.types.ObjectId

class UserSettingsNotFoundException(message: String?, cause: Throwable?) : AbstractException() {
    override val message: String = message ?: "User settings not found"
    override val status: Int = 404
    override val cause: Throwable = cause ?: Throwable(message)
    override val code = ExceptionCode.USER_SETTINGS_NOT_FOUND

    constructor() : this(null, null)
    constructor(userId: ObjectId) : this("User settings not found for user $userId", null)
    constructor(message: String) : this(message, null)
    constructor(ex: Exception) : this(ex.message, ex.cause)
}