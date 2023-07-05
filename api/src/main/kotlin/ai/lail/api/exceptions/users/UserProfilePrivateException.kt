package ai.lail.api.exceptions.users

import ai.lail.api.exceptions.AbstractException
import ai.lail.api.exceptions.ExceptionCode
import org.bson.types.ObjectId
import org.springframework.http.HttpStatus

class UserProfilePrivateException(message: String?, cause: Throwable?) : AbstractException() {
    override val message: String = message ?: "User profile is private"
    override val status: Int = HttpStatus.BAD_REQUEST.value()
    override val cause: Throwable = cause ?: Throwable(message)
    override val code = ExceptionCode.USER_PROFILE_PRIVATE

    constructor() : this(null, null)
}
