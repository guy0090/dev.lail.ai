package ai.lail.api.services

import ai.lail.api.data.settings.user.UserSettings
import ai.lail.api.dto.requests.users.UserSettingResetDto
import ai.lail.api.dto.requests.users.UserSettingsUpdateDto
import ai.lail.api.enums.PrivacySetting
import ai.lail.api.exceptions.settings.users.UserSettingCollisionException
import ai.lail.api.exceptions.settings.users.UserSettingsNotFoundException
import ai.lail.api.repositories.UserSettingsRepository
import ai.lail.api.util.CacheHelper
import ai.lail.api.util.ObjectIdHelper
import org.bson.types.ObjectId
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
@CacheConfig(cacheNames = [CacheHelper.USER_SETTINGS_CACHE])
class UserSettingsService(val userSettingsRepository: UserSettingsRepository) {

    @Cacheable(key = "#userId", unless = "#result == null")
    fun find(userId: ObjectId): UserSettings? =
        userSettingsRepository.get(userId).orElse(null)

    @Cacheable(key = "#userId", unless = "#result == null")
    fun findFromString(userId: String, privacy: List<PrivacySetting>): UserSettings? =
        userSettingsRepository.get(ObjectIdHelper.getId(userId)).orElse(null)

    fun findFromCustomUrl(slug: String): UserSettings? =
        userSettingsRepository.findByCustomUrlSlug(slug).orElse(null)

    fun findMany(userIds: Collection<ObjectId>, privacy: List<PrivacySetting>): List<UserSettings> {
        return userSettingsRepository.getMany(userIds, privacy)
    }

    @CachePut(key = "#userId")
    fun create(userId: ObjectId): UserSettings {
        val userSettings = UserSettings(userId)
        return userSettingsRepository.save(userSettings)
    }

    @CacheEvict(key = "#userId")
    @Throws(UserSettingCollisionException::class)
    fun update(userId: ObjectId, update: UserSettingsUpdateDto): UserSettings {
        val userSettings = find(userId) ?: throw UserSettingsNotFoundException()
        // check if custom URL slug is unique
        if (update.customUrlSlug != null) verifySlugUnique(userId, update.customUrlSlug)

        val changed = userSettings.update(update)
        if (changed) return userSettingsRepository.save(userSettings)
        return userSettings
    }

    @CacheEvict(key = "#userId")
    fun reset(userId: ObjectId, update: UserSettingResetDto): UserSettings {
        val userSettings = find(userId) ?: throw UserSettingsNotFoundException()
        val changed = userSettings.reset(update)
        if (changed) return userSettingsRepository.save(userSettings)
        return userSettings
    }

    /**
     * Verifies that the custom URL slug is unique.
     *
     * Attempts to find another settings object where the slug matches the lookup slug,
     * and throws an exception if that settings object does not belong to the user requesting the update.
     *
     * @param subject The ID of the user whose settings are being updated.
     * @param urlSlug The custom URL slug to verify.
     */
    @Throws(UserSettingCollisionException::class)
    fun verifySlugUnique(subject: ObjectId, urlSlug: String) {
        val potentialCollision = findFromCustomUrl(urlSlug) ?: throw UserSettingsNotFoundException()
        if (potentialCollision.id != subject) {
            throw UserSettingCollisionException("Custom URL slug already exists")
        }
    }
}