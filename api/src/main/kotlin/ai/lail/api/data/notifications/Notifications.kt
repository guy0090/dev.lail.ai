package ai.lail.api.data.notifications

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("notifications")
data class Notifications(
    @Id
    val id: ObjectId = ObjectId(),
    val notifications: MutableSet<Notification> = mutableSetOf()
)