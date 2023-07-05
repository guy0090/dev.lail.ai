package ai.lail.api.services

import ai.lail.api.data.discord.DiscordUser
import ai.lail.api.data.users.User
import ai.lail.api.dto.responses.users.UserDto
import ai.lail.api.enums.PrivacySetting
import ai.lail.api.repositories.UserRepository
import ai.lail.api.util.CacheHelper
import org.bson.types.ObjectId
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Service

@Service
@CacheConfig(cacheNames = [CacheHelper.USERS_CACHE])
class UserService(
    val userRepository: UserRepository,
    val mongoTemplate: MongoTemplate,
    val userSettingsService: UserSettingsService
) {

    fun count(): Long {
        return userRepository.count()
    }

    fun create(user: User): User {
        return userRepository.save(user)
    }

    @Cacheable(key = "#id", unless = "#result == null")
    fun find(id: ObjectId): User? {
        return userRepository.findById(id.toHexString()).orElse(null)
    }

    fun findMany(ids: Collection<ObjectId>): List<User> {
        return userRepository.findAllById(ids.map { it.toHexString() })
    }

    @Cacheable(key = "#discordId", unless = "#result == null")
    fun findByDiscordId(discordId: String): User? {
        return userRepository.findByDiscordId(discordId).orElse(null)
    }

    @CachePut(key = "#userId")
    fun updateFromDiscord(userId: ObjectId, discordUpdate: DiscordUser): User {
        val existing = find(userId) ?: throw Exception("User not found")
        val updated = existing.updateFromDiscord(discordUpdate)
        return userRepository.save(updated)
    }

    @CacheEvict(key = "#id")
    fun delete(id: String) {
        userRepository.deleteById(id)
    }

    fun exists(id: String): Boolean {
        return userRepository.existsById(id)
    }

    fun exists(id: ObjectId): Boolean {
        return userRepository.existsById(id.toHexString())
    }

    /// Misc. helpers

    fun getAllIds(): List<ObjectId> {
        val query = Query()
        query.fields().include("_id")
        return mongoTemplate.find(query, User::class.java).map { it.id }
    }

    fun getUserDto(id: ObjectId): UserDto? {
        val settings = userSettingsService.find(id) ?: return null
        val user = find(settings.id) ?: return null

        return UserDto(user, settings)
    }

    fun getUserDtoFromSlug(slug: String, privacy: List<PrivacySetting> = ALL_PRIVACY_SETTINGS): UserDto? {
        val settings = userSettingsService.findFromCustomUrl(slug) ?: return null
        val user = find(settings.id) ?: return null
        return UserDto(user, settings)
    }

    fun getManyUserDto(ids: Collection<ObjectId>, privacy: List<PrivacySetting> = ALL_PRIVACY_SETTINGS): List<UserDto> {
        if (ids.isEmpty()) return emptyList()
        val settings = userSettingsService.findMany(ids, privacy)
        val users = findMany(settings.map { it.id })

        return users.mapNotNull { user ->
            val setting = settings.find { it.id == user.id } ?: return@mapNotNull null
            UserDto(user, setting)
        }
    }

    companion object {
        val ALL_PRIVACY_SETTINGS = listOf(
            PrivacySetting.PUBLIC,
            PrivacySetting.PRIVATE,
            PrivacySetting.UNLISTED
        )
    }

}