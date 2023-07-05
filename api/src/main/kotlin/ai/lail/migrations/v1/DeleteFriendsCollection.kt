package ai.lail.migrations.v1

import io.mongock.api.annotations.ChangeUnit
import io.mongock.api.annotations.Execution
import io.mongock.api.annotations.RollbackExecution
import org.springframework.data.mongodb.core.MongoTemplate

@ChangeUnit(id = "delete-friends-collection", order = "3", author = "lailai")
class DeleteFriendsCollection {

    @Execution
    fun execution(mongoTemplate: MongoTemplate) {
        mongoTemplate.dropCollection("friends")
    }

    @RollbackExecution
    fun rollback() {
        // do nothing
    }
}