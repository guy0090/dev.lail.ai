package ai.lail.api.services

import ai.lail.api.data.discord.DiscordUser
import ai.lail.api.data.settings.system.Settings
import ai.lail.api.data.users.User
import ai.lail.api.dto.responses.users.UserDetailsDto
import ai.lail.api.dto.responses.users.UserSelfDto
import ai.lail.api.enums.ServiceStatus
import ai.lail.api.exceptions.settings.system.SystemSettingsNotFoundException
import ai.lail.api.repositories.AdminRepository
import ai.lail.api.util.CacheHelper
import org.bson.types.ObjectId
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.CachePut
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
@CacheConfig(cacheNames = [CacheHelper.SYSTEM_SETTINGS_CACHE])
class AdminService(
    val adminRepository: AdminRepository,
    val redisTemplate: RedisTemplate<String, Any>,
    val encounterService: EncounterService,
    val encounterSummaryService: EncounterSummaryService,
    val vaultService: VaultService,
    val userService: UserService,
    val userSettingsService: UserSettingsService,
    val permissionService: PermissionService,
    val notificationService: NotificationService,
    val followerService: FollowerService,
    val roleService: RoleService
) {

    fun getSettings(): Settings {
        return adminRepository.findById(Settings.STATIC_ID.toHexString())
            .orElseThrow { SystemSettingsNotFoundException() }
    }

    @CachePut(key = "#settings.id", unless = "#result == null")
    fun saveSettings(settings: Settings): Settings {
        return adminRepository.save(settings)
    }

    fun isInitialized(): Boolean {
        val settings = getSettings()
        return settings.initDone
    }

    fun createNewUser(user: DiscordUser): User {
        val settings = getSettings()
        return createNewUser(User.fromDiscordUser(user, settings.defaults.defaultMaxUploads))
    }

    fun createNewUser(user: User): User {
        val created = userService.create(user)
        vaultService.createUserSecrets(created.id) // Create salt
        userSettingsService.create(created.id)  // Create settings
        permissionService.create(created.id) // Create permissions
        notificationService.create(created.id) // Create notification
        followerService.create(created.id) // Create followers

        return created
    }

    //// ----------------- Directly uses RedisTemplate ----------------- ////
    /// Used to set the system status, which is used to enable/disable all routes
    /// until the service is ready to accept requests.
    //// ----------------- --------------------------- ----------------- ////

    fun setSystemStatus(stage: ServiceStatus) {
        redisTemplate.opsForValue().set("startup", stage.value())
    }

    fun getSystemStatus(): ServiceStatus {
        redisTemplate.opsForValue().get("startup").let { status ->
            return if (status == null) ServiceStatus.UNKNOWN
            else ServiceStatus.fromInt((status as String).toInt())
        }
    }

    /**
     * Gets the number of entries in the Encounter, User, and Role repositories.
     * @return A map of each of the specified repositories and their entries.
     */
    fun getCollectionStatus() : Map<String, Long>{
        val map = mutableMapOf<String, Long>()
        map["encounters"] = encounterService.count()
        map["users"] = userService.count()
        map["roles"] = roleService.count()
        return map
    }

    fun getUserDetails(userId: ObjectId): UserDetailsDto {
        val user = userService.find(userId) ?: throw Exception("User not found")
        val userSettings = userSettingsService.find(userId) ?: throw Exception("User settings not found")
        val permissions = permissionService.getUserPermissions(userId)
        val uploadCount = encounterSummaryService.countByOwner(userId)

        return UserDetailsDto(UserSelfDto(user, uploadCount), userSettings, permissions)
    }
}