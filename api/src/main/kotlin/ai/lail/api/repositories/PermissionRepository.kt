package ai.lail.api.repositories

import ai.lail.api.data.permissions.Permissions
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface PermissionRepository : MongoRepository<Permissions, String> {
    fun findById(userId: ObjectId): Permissions?
}