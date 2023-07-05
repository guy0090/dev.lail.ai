package ai.lail.api.repositories

import ai.lail.api.data.notifications.Notifications
import org.springframework.data.mongodb.repository.MongoRepository

interface NotificationRepository : MongoRepository<Notifications, String>