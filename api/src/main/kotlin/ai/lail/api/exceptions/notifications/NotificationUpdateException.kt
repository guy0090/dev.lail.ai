package ai.lail.api.exceptions.notifications

import ai.lail.api.exceptions.AbstractException
import ai.lail.api.exceptions.ExceptionCode
import org.bson.types.ObjectId
import org.springframework.http.HttpStatus

class NotificationUpdateException(message: String?, cause: Throwable?) : AbstractException() {
    override val message: String = message ?: "Failed to update notification"
    override val status: Int = HttpStatus.BAD_REQUEST.value()
    override val cause: Throwable = cause ?: Throwable(message)
    override val code = ExceptionCode.NOTIFICATION_UPDATE_FAILED

    constructor(reason: String) : this(reason, null)
    constructor(userId: ObjectId, notificationId: ObjectId)
            : this(
        "Notification (${notificationId.toHexString()}) for user with ID ${userId.toHexString()} could not be updated",
        null
    )
}