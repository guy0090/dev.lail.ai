package ai.lail.api.exceptions.notifications

import ai.lail.api.exceptions.AbstractException
import ai.lail.api.exceptions.ExceptionCode
import org.bson.types.ObjectId
import org.springframework.http.HttpStatus

class NotificationsNotFoundException(message: String?, cause: Throwable?) : AbstractException() {
    override val message: String = message ?: "Notification not found"
    override val status: Int = HttpStatus.BAD_REQUEST.value()
    override val cause: Throwable = cause ?: Throwable(message)
    override val code = ExceptionCode.NOTIFICATION_NOT_FOUND

    constructor(userId: ObjectId) : this("Notification for user with ID ${userId.toHexString()} not found.", null)
}