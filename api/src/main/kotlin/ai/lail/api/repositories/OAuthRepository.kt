package ai.lail.api.repositories

import ai.lail.api.data.discord.DiscordOAuth
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface OAuthRepository : MongoRepository<DiscordOAuth, String>