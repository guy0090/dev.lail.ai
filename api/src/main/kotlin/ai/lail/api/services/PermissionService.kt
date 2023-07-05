package ai.lail.api.services

import ai.lail.api.config.services.CacheConfigurationService
import ai.lail.api.data.permissions.Permissions
import ai.lail.api.data.permissions.Role
import ai.lail.api.enums.UpdateType
import ai.lail.api.exceptions.permissions.*
import ai.lail.api.permissions.Permission
import ai.lail.api.repositories.PermissionRepository
import ai.lail.api.util.CacheHelper
import ai.lail.api.util.getAuthUser
import jakarta.servlet.http.HttpServletRequest
import org.bson.types.ObjectId
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
@CacheConfig(cacheNames = [CacheHelper.PERMISSIONS_CACHE])
class PermissionService(
    val permissionRepository: PermissionRepository,
    val roleService: RoleService,
    val redisTemplate: RedisTemplate<String, Any>,
    val cacheConfigurationService: CacheConfigurationService,
    val mongoTemplate: MongoTemplate
) {
    val logger: Logger = LoggerFactory.getLogger(PermissionService::class.java)


    fun count(): Long = permissionRepository.count()


    @CachePut(key = "#userId", unless = "#result == null")
    fun create(userId: ObjectId): Permissions {
        val defaultRoles = roleService.getDefaultRoles().map { it.id }.toMutableSet()
        val userPerms = Permissions(userId, roles = defaultRoles)
        return permissionRepository.save(userPerms)
    }


    @Cacheable(key = "#userId", unless = "#result == null")
    fun find(userId: ObjectId): Permissions? = permissionRepository.findById(userId)


    @CachePut(key = "#permissions.id")
    fun update(permissions: Permissions): Permissions = permissionRepository.save(permissions)


    /**
     * @return `true` if the permission was added, false if it wasn't
     */
    @CacheEvict(key = "#userId")
    fun addPermission(userId: ObjectId, permission: String): Permissions {
        val permissions = find(userId) ?: throw PermissionsNotFoundException(userId)
        permissions.addPermission(permission)
        return update(permissions)
    }


    @CacheEvict(key = "#userId")
    fun addPermissions(userId: ObjectId, toAdd: Set<String>): Permissions {
        val permissions = find(userId) ?: throw PermissionsNotFoundException(userId)
        permissions.addPermissions(toAdd.toList())
        return update(permissions)
    }


    @CacheEvict(key = "#userId")
    fun removePermission(userId: ObjectId, permission: String): Permissions {
        val permissions = find(userId) ?: throw PermissionsNotFoundException(userId)
        permissions.removePermission(permission)
        return update(permissions)
    }


    @CacheEvict(key = "#userId")
    fun removePermissions(userId: ObjectId, toRemove: Set<String>): Permissions {
        val permissions = find(userId) ?: throw PermissionsNotFoundException(userId)
        permissions.removePermissions(toRemove)
        return update(permissions)
    }


    @CacheEvict(key = "#userId")
    fun modifyPermissions(userId: ObjectId, permissions: Set<String>, type: UpdateType): Permissions {
        return when (type) {
            UpdateType.ADD -> addPermissions(userId, permissions)
            UpdateType.REMOVE -> removePermissions(userId, permissions)
        }
    }


    /**
     * Adds a role to a user
     * @param userId The user who is receiving a role.
     * @param role The role that is being added to the user.
     * @return The updated permissions.
     */
    @Throws(PermissionsNotFoundException::class, AddRoleToUserException::class)
    @CacheEvict(key = "#userId")
    fun addRole(userId: ObjectId, role: ObjectId): Permissions {
        val permissions = find(userId) ?: throw PermissionsNotFoundException(userId)
        if (permissions.hasRole(role) || !roleService.exists(role)) throw AddRoleToUserException()
        permissions.addRole(role)
        return update(permissions)
    }


    /**
     * Adds multiple roles to a user.
     *
     * @param userId The user who is receiving the roles.
     * @param roles The roles that are being added to the user.
     * @return The updated permissions.
     *
     * @throws PermissionsNotFoundException if the user permissions don't exist.
     * @throws RoleNotFoundException if any of the roles do not exist.
     */
    @CacheEvict(key = "#userId")
    @Throws(PermissionsNotFoundException::class, RoleNotFoundException::class)
    fun addRoles(userId: ObjectId, roles: Set<ObjectId>): Permissions {
        val permissions = find(userId) ?: throw PermissionsNotFoundException(userId)
        if (!roleService.allExists(roles)) throw RoleNotFoundException()

        permissions.addRoles(roles)
        return update(permissions)
    }


    /**
     * Removes a role from a user
     * @param userId The user whose role is being removed.
     * @param role The role that is being removed from the user.
     * @return The updated permissions.
     */
    @Throws(PermissionsNotFoundException::class, AddRoleToUserException::class)
    @CacheEvict(key = "#userId")
    fun removeRole(userId: ObjectId, role: ObjectId): Permissions {
        val permissions = find(userId) ?: throw PermissionsNotFoundException(userId)
        if (!permissions.hasRole(role) || !roleService.exists(role)) throw RemoveRoleFromUserException()
        permissions.removeRole(role)
        return update(permissions)
    }


    /**
     * @return The updated [Permissions] object
     */
    @CacheEvict(key = "#userId")
    fun removeRoles(userId: ObjectId, roles: Set<ObjectId>): Permissions {
        val permissions = find(userId) ?: throw PermissionsNotFoundException(userId)
        permissions.removeRoles(roles)
        return update(permissions)
    }


    @CacheEvict(key = "#userId")
    fun modifyRoles(userId: ObjectId, roles: Set<ObjectId>, type: UpdateType): Permissions {
        return when (type) {
            UpdateType.ADD -> addRoles(userId, roles)
            UpdateType.REMOVE -> removeRoles(userId, roles)
        }
    }


    @CacheEvict(key = "#userId")
    fun delete(userId: ObjectId) {
        permissionRepository.deleteById(userId.toHexString())
    }


    /**
     * Removes specified role from every user then deletes the role.
     * @param roleId The role to be deleted as ObjectId
     */
    fun deleteRole(roleId: ObjectId) {
        roleService.canDelete(roleId)

        val criteria = Criteria.where("roles").`is`(roleId)
        val update = Update().pull("roles", roleId)
        val query = Query.query(criteria)

        val result = mongoTemplate.updateMulti(query, update, Permissions::class.java)
        if (result.modifiedCount != result.matchedCount) throw RemoveDeletedRoleFromUserException()
        roleService.delete(roleId)
    }


    fun exists(roleId: ObjectId): Boolean {
        return roleService.exists(roleId)
    }


    fun getUserRoles(userId: ObjectId): List<Role> {
        val permissionsBase = find(userId) ?: return listOf()
        return roleService.findAllById(permissionsBase.roles.toList())
    }


    fun getUserRoles(permissions: Permissions): List<Role> = roleService.findAllById(permissions.roles.toList())


    fun getUserPermissions(userId: ObjectId): Set<String> {
        val permissionsBase = find(userId) ?: return setOf()
        if (!isCached(userId)) {
            val key = "${CacheHelper.PERMISSIONS_CACHE}::$userId"
            redisTemplate.opsForValue().set(key, permissionsBase, getCacheTTL(), TimeUnit.SECONDS)
        }

        val perms = permissionsBase.permissions.toList()
        val roles = getUserRoles(permissionsBase)
        return listOf(roleService.getAllPermissions(roles, null).toList(), perms).flatten().toSet()
    }


    fun hasPermission(userId: ObjectId, permission: String?): Boolean {
        val permissions = getUserPermissions(userId)
        return hasPermission(permission, permissions)
    }

    fun hasPermission(permission: String?, userPermissions: Set<String>): Boolean {
        if (permission == null || permission == "" || userPermissions.contains(Permission.ROOT)) return true
        if (userPermissions.isEmpty()) return false

        val nodes = permission.split(".")
        for (i in nodes.indices) {
            val node = nodes.subList(0, i + 1).joinToString(".")
            if (userPermissions.contains(node)) return true
        }
        return false
    }


    private fun getCacheTTL(): Long {
        val expiration = cacheConfigurationService.getExpirations()
        return expiration.permissions
    }

    private fun isCached(userId: ObjectId): Boolean = redisTemplate.hasKey("${CacheHelper.PERMISSIONS_CACHE}::$userId")

    /**
     * Attempt to change a value if the user has the specified permission.
     * If the user does not have the permission, the default value is returned.
     * The default value is also returned if the new value is null or equal to the default value.
     *
     * @param permission The permission to check for.
     * @param permissions The user's permissions.
     * @param newValue The new value to set if the user has the permission.
     * @param defaultValue The default value to set if the user does not have the permission.
     *
     * @return The new value if the user has the permission, the default value otherwise.
     */
    fun <T> permittedChange(permission: String, permissions: Set<String>, newValue: T?, defaultValue: T): T {
        if (newValue == null || newValue == defaultValue) return defaultValue
        return if (hasPermission(permission, permissions)) newValue else defaultValue
    }

    /**
     * Attempt to change a value if the user has the specified permission.
     * If the user does not have the permission, the default value is returned.
     * The default value is also returned if the new value is null or equal to the default value.
     *
     * @param permission The permission to check for.
     * @param request The request to get the user from.
     * @param newValue The new value to set if the user has the permission.
     * @param defaultValue The default value to set if the user does not have the permission.
     *
     * @return The new value if the user has the permission, the default value otherwise.
     */
    fun <T> permittedChange(permission: String, request: HttpServletRequest, newValue: T?, defaultValue: T): T {
        val authUser = request.getAuthUser() ?: return defaultValue
        return permittedChange(permission, authUser.permissions, newValue, defaultValue)
    }
}