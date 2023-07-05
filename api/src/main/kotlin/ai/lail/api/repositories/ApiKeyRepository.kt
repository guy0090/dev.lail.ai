package ai.lail.api.repositories

import ai.lail.api.data.keys.ApiKey
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface ApiKeyRepository : MongoRepository<ApiKey, String> {

    fun findById(id: ObjectId): ApiKey?
    fun findByKey(key: String): ApiKey?
    fun findAllByOwner(owner: ObjectId): List<ApiKey>?
    fun findByIdAndOwner(id: ObjectId, owner: ObjectId): ApiKey?
    fun deleteById(key: ObjectId)
    fun existsById(id: ObjectId): Boolean
}