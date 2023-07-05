package ai.lail.api.repositories

import ai.lail.api.data.users.User
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.Optional

/**
 * Repository for managing users via. MongoDB.
 */
@Repository
interface UserRepository : MongoRepository<User, String> {
    fun findByDiscordId(discordId: String): Optional<User>
}