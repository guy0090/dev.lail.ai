package ai.lail.api.repositories

import ai.lail.api.data.guilds.Guild
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface GuildRepository : MongoRepository<Guild, String> {
    fun findByOwner(owner: ObjectId): Guild?
}