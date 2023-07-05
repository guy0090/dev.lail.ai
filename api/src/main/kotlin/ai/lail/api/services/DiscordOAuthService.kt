package ai.lail.api.services

import ai.lail.api.data.discord.DiscordOAuth
import ai.lail.api.data.discord.DiscordOAuthGrant
import ai.lail.api.data.users.User
import ai.lail.api.repositories.OAuthRepository
import org.bson.types.ObjectId
import org.springframework.stereotype.Service

/**
 * Service responsible for managing Discord OAuth grants.
 * @see DiscordOAuth
 * @see DiscordOAuthGrant
 * @see OAuthRepository
 */
@Service
class DiscordOAuthService(val discordApiService: DiscordApiService, val oAuthRepository: OAuthRepository) {

    /**
     * Saves an OAuth grant.
     */
    fun save(userId: ObjectId, grant: DiscordOAuthGrant): DiscordOAuth {
        val auth = DiscordOAuth.fromGrant(userId, grant)
        return oAuthRepository.save(auth)
    }

    fun save(user: User, grant: DiscordOAuthGrant) = save(user.id, grant)


    /**
     * Finds an OAuth grant by its user ID.
     */
    fun findById(userId: ObjectId): DiscordOAuth? = oAuthRepository.findById(userId.toHexString()).orElse(null)

    /**
     * Refreshes an OAuth grant.
     */
    fun refresh(userOAuth: DiscordOAuth): DiscordOAuth? {
        val grant = discordApiService.refreshToken(userOAuth.refresh)
        return if (grant != null) save(userOAuth.id, grant) else null
    }

    /**
     * Deletes an OAuth grant by its user ID.
     */
    fun delete(userId: String) {
        oAuthRepository.deleteById(userId)
    }

    /**
     * Deletes an OAuth grant by its user ID.
     */
    fun delete(userId: ObjectId) {
        oAuthRepository.deleteById(userId.toHexString())
    }

    /**
     * Finds all expired OAuth grants.
     */
    fun findExpired(): List<DiscordOAuth> {
        return oAuthRepository.findAll().filter { it.expiresAt <= System.currentTimeMillis() }
    }

    /**
     * Refreshes all expired OAuth grants.
     */
    fun refreshExpired() {
        val grants = findExpired()
        grants.forEach { refresh(it) }
    }
}