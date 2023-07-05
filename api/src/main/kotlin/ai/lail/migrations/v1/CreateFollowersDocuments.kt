package ai.lail.migrations.v1

import ai.lail.api.services.FollowerService
import ai.lail.api.services.NotificationService
import ai.lail.api.services.UserService
import io.mongock.api.annotations.ChangeUnit
import io.mongock.api.annotations.Execution
import io.mongock.api.annotations.RollbackExecution
import org.bson.types.ObjectId
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@ChangeUnit(id = "create-followers-documents", order = "4", author = "lailai")
class CreateFollowersDocuments {
    val logger: Logger = LoggerFactory.getLogger(CreateFollowersDocuments::class.java)
    private val created = mutableListOf<ObjectId>()

    @Execution
    fun execution(users: UserService, followers: FollowerService) {
        val userIds = users.getAllIds()
        userIds.forEach { id ->
            val hasFollowersObj = followers.exists(id)
            if (!hasFollowersObj) {
                followers.create(id)
                logger.info("[mongock] Created followers object for user $id")
                created.add(id)
            }
        }

    }

    @RollbackExecution
    fun rollback(followers: FollowerService) {
        followers.deleteMany(created)
    }
}