package ai.lail.migrations.v1

import ai.lail.api.services.NotificationService
import ai.lail.api.services.UserService
import io.mongock.api.annotations.ChangeUnit
import io.mongock.api.annotations.Execution
import io.mongock.api.annotations.RollbackExecution
import org.bson.types.ObjectId
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@ChangeUnit(id = "create-notification-documents", order = "2", author = "lailai")
class CreateNotificationDocuments {
    val logger: Logger = LoggerFactory.getLogger(CreateNotificationDocuments::class.java)
    private val created = mutableListOf<ObjectId>()

    @Execution
    fun execution(users: UserService, notifications: NotificationService) {
        val userIds = users.getAllIds()
        userIds.forEach { id ->
            val hasNotificationObj = notifications.exists(id)
            if (!hasNotificationObj) {
                notifications.create(id)
                logger.info("[mongock] Created notification object for user $id")
                created.add(id)
            }
        }

    }

    @RollbackExecution
    fun rollback(notifications: NotificationService) {
        notifications.deleteAll(created)
    }
}