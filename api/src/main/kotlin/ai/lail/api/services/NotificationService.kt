package ai.lail.api.services

import ai.lail.api.data.notifications.Notification
import ai.lail.api.data.notifications.Notifications
import ai.lail.api.exceptions.notifications.NotificationUpdateException
import ai.lail.api.exceptions.notifications.NotificationsNotFoundException
import ai.lail.api.repositories.NotificationRepository
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class NotificationService(
    val notificationRepository: NotificationRepository,
    val mongoTemplate: MongoTemplate
) {


    fun create(userId: ObjectId): Notifications {
        val notification = Notifications(id = userId)
        return notificationRepository.save(notification)
    }


    fun get(userId: ObjectId): Notifications? {
        return notificationRepository.findByIdOrNull(userId.toHexString())
    }


    fun exists(userId: ObjectId): Boolean {
        return notificationRepository.existsById(userId.toHexString())
    }


    fun deleteAll(ids: List<ObjectId>) {
        notificationRepository.deleteAllById(ids.map { it.toHexString() })
    }


    fun clearNotification(userId: ObjectId, notificationId: ObjectId) {
        val exists = exists(userId)
        if (!exists) throw NotificationsNotFoundException(userId)

        val criteria = Criteria.where("_id").`is`(userId)
        val update = Update().pull("notifications", Query.query(Criteria.where("_id").`is`(notificationId)))
        val query = Query.query(criteria)

        val result = mongoTemplate.updateFirst(query, update, Notifications::class.java)
        if (result.modifiedCount == 0L) throw NotificationUpdateException(userId, notificationId)
    }


    fun clearNotifications(userId: ObjectId, notificationIds: Set<ObjectId>): Collection<Notification> {
        val exists = exists(userId)
        if (!exists) throw NotificationsNotFoundException(userId)

        val criteria = Criteria.where("_id").`is`(userId)
        val update = Update().pullAll("notifications", notificationIds.toTypedArray())
        val query = Query.query(criteria)

        val result = mongoTemplate.updateFirst(query, update, Notifications::class.java)
        if (result.modifiedCount == 0L) throw NotificationUpdateException("Could not clear notifications for user $userId")
        return get(userId)?.notifications ?: throw NotificationsNotFoundException(userId)
    }


    fun clearAllNotifications(userId: ObjectId) {
        val exists = exists(userId)
        if (!exists) throw NotificationsNotFoundException(userId)

        val criteria = Criteria.where("_id").`is`(userId)
        val update = Update().set("notifications", emptyList<Notification>())
        val query = Query.query(criteria)

        mongoTemplate.updateFirst(query, update, Notifications::class.java)
        // No checks necessary, since we're clearing all notifications and don't
        // care if the user had any notifications in the first place (exists check above)
    }


    fun addNotification(userId: ObjectId, notification: Notification) {
        val exists = exists(userId)
        if (!exists) throw NotificationsNotFoundException(userId)

        val criteria = Criteria.where("_id").`is`(userId)
        val update = Update().push("notifications", notification)
        val query = Query.query(criteria)

        val result = mongoTemplate.updateFirst(query, update, Notifications::class.java)
        if (result.modifiedCount == 0L) throw NotificationUpdateException(userId, notification.id)
    }


    fun getUserNotifications(userId: ObjectId): List<Notification> {
        val notifications = get(userId)?.notifications ?: throw NotificationsNotFoundException(userId)
        return notifications.toList()
    }


    fun setNotificationSeen(userId: ObjectId, notificationId: ObjectId) {
        val exists = exists(userId)
        if (!exists) throw NotificationsNotFoundException(userId)

        val criteria = Criteria.where("_id").`is`(userId).and("notifications._id").`is`(notificationId)
        val update = Update().set("notifications.$.seen", true)
        val query = Query.query(criteria)

        val result = mongoTemplate.updateFirst(query, update, Notifications::class.java)
        if (result.modifiedCount == 0L) throw NotificationUpdateException(userId, notificationId)
    }
}