package ai.lail.api.services

import ai.lail.api.data.guilds.Guild
import ai.lail.api.data.guilds.GuildMember
import ai.lail.api.data.guilds.GuildRole
import ai.lail.api.dto.responses.users.UserDto
import ai.lail.api.exceptions.*
import ai.lail.api.exceptions.guilds.*
import ai.lail.api.exceptions.users.UserAlreadyInGuildException
import ai.lail.api.permissions.nodes.UserGuildPermission
import ai.lail.api.repositories.GuildRepository
import ai.lail.api.util.CacheHelper
import ai.lail.api.util.ObjectIdHelper
import org.bson.types.ObjectId
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

/**
 * Service for managing guilds.
 *
 * - By design, all methods that *modify* any aspect of a guild will throw a custom [AbstractException] if the guild is not found.
 * - Any method that *reads* an aspect of a guild will return null if the guild is not found.
 *
 * @see GuildNotFoundException
 * @See GuildRemoveOwnerException
 */
@Service
@CacheConfig(cacheNames = [CacheHelper.GUILDS_CACHE])
class GuildService(val guildRepository: GuildRepository) {
    val logger: Logger = LoggerFactory.getLogger(GuildService::class.java)

    /**
     * Find a guild by its ID. Returns null if the guild is not found.
     *
     * @param guildId The ID of the guild to search for.
     */
    @Cacheable(key = "#guildId", unless = "#result == null")
    fun findGuildById(guildId: ObjectId): Guild? {
        return guildRepository.findById(guildId.toHexString()).orElse(null)
    }

    /**
     * Finds guilds a user is the owner of.
     * If the user isn't the owner of any guilds, an empty list is returned.
     *
     * @param userId The ID of the user to search for.
     */
    @Cacheable(key = "#result.id", unless = "#result == null")
    fun findGuildByOwner(userId: ObjectId): Guild? {
        return guildRepository.findByOwner(userId)
    }

    /**
     * Find the guild a user is a member of.
     * If the user isn't in any guild, null is returned.
     *
     * @param userId The ID of the user to search for.
     */
    @Cacheable(key = "#result.id", unless = "#result == null")
    fun findGuildUserIsIn(userId: ObjectId): Guild? {
        val memberOf = guildRepository.findAll().filter { guild -> guild.hasMember(userId) }
        return if (memberOf.isEmpty()) null
        else memberOf.first()
    }

    /**
     * Returns a guild member from a guild. Returns null if the guild or member is not found.
     *
     * @param guildId The ID of the guild to search.
     * @param userId The ID of the user to search for.
     */
    fun findMemberFromGuild(guildId: ObjectId, userId: ObjectId): GuildMember? {
        findGuildById(guildId).let { guild ->
            return if (guild == null) null
            else return guild.getMemberById(userId)
        }
    }

    /**
     * Get a guild's roles. Returns an empty list if the guild is not found.
     *
     * @param guildId The ID of the guild return [GuildRole]s for.
     */
    fun getGuildRoles(guildId: ObjectId): List<GuildRole>? {
        findGuildById(guildId).let { guild ->
            return guild?.roles?.toList() ?: listOf()
        }
    }

    /**
     * Modifies the name of an existing guild. If the guild isn't found, an exception is thrown
     * otherwise the updated guild is returned.
     *
     * @throws GuildNotFoundException If the guild is not found.
     *
     * @param guildId The ID of the guild to modify.
     * @param name The new name for the guild.
     */
    @Throws(GuildNotFoundException::class)
    @CachePut(key = "#guildId")
    fun saveGuildName(guildId: ObjectId, name: String): Guild {
        findGuildById(guildId).let { guild ->
            if (guild == null) {
                logger.error("GuildService.saveGuildName() - Guild not found: $guildId")
                throw GuildNotFoundException(guildId)
            }

            guild.name = name
            return saveGuild(guild)
        }
    }

    /**
     * Updates a guild's icon. Returns the updated guild if successful.
     * If the guild isn't found, an exception is thrown.
     *
     * @throws GuildNotFoundException If the guild is not found.
     *
     * @param guildId The ID of the guild to modify.
     * @param icon The new icon for the guild. TODO: Decide on encoding or directly link to image.
     */
    @Throws(GuildNotFoundException::class)
    @CachePut(key = "#guildId")
    fun saveGuildIcon(guildId: ObjectId, icon: String): Guild {
        findGuildById(guildId).let { guild ->
            if (guild == null) {
                logger.error("GuildService.saveGuildIcon() - Guild not found: $guildId")
                throw GuildNotFoundException(guildId)
            }

            guild.icon = icon
            return saveGuild(guild)
        }
    }

    /**
     * Adds a **new** member to a guild. Returns the new guild if successful.
     * If the guild isn't found or a member is already in the guild, an exception will be thrown.
     *
     * @throws UserAlreadyInGuildException If the user is already in a guild.
     * @throws GuildNotFoundException If the guild is not found.
     * @throws GuildMemberAlreadyInGuildException If the user is already a member of the guild.
     *
     * @param guildId The ID of the guild to add the member to.
     * @param user The user to add to the guild.
     */
    @Throws(
        UserAlreadyInGuildException::class,
        GuildNotFoundException::class,
        GuildMemberAlreadyInGuildException::class
    )
    @CachePut(key = "#guildId", unless = "#result == null")
    fun addMemberToGuild(guildId: ObjectId, user: UserDto): Guild {
        val userId = ObjectIdHelper.getId(user.id)
        if (findGuildUserIsIn(userId) != null) throw UserAlreadyInGuildException(userId)

        findGuildById(guildId).let { guild ->
            if (guild == null) {
                logger.error("GuildService.addMemberToGuild() - Guild '${guildId.toHexString()}' not found")
                throw GuildNotFoundException(guildId)
            }
            if (guild.hasMember(userId)) {
                logger.error("GuildService.addMemberToGuild() - User '${user.id}' is already a member of guild '${guildId.toHexString()}'")
                throw GuildMemberAlreadyInGuildException(userId, guildId)
            }

            logger.info("Adding user ${user.id} to guild ${guild.id}")
            val newMember = GuildMember(userId, user.username)
            guild.addMember(newMember)
            return saveGuild(guild)
        }
    }

    /**
     * Removes a user (member) from a guild. Returns the new guild if successful.
     * If the user being removed is the owner of the guild or not a member, an exception is thrown.
     *
     * @throws GuildNotFoundException If the guild isn't found.
     * @throws GuildRemoveOwnerException If the user being removed is the owner of the guild.
     * @throws GuildMemberNotFoundException If the user isn't a member of the guild.
     *
     * @param guildId The ID of the guild to modify.
     * @param userId The ID of the user to remove.
     */
    @Throws(GuildRemoveOwnerException::class, GuildNotFoundException::class, GuildMemberNotFoundException::class)
    @CachePut(key = "#guildId", unless = "#result == null")
    fun removeMemberFromGuild(guildId: ObjectId, userId: ObjectId): Guild {
        findGuildById(guildId).let { guild ->
            if (guild == null) {
                logger.error("GuildService.removeMemberFromGuild() - Guild '${guildId.toHexString()}' not found")
                throw GuildNotFoundException(guildId)
            }
            if (guild.owner == userId) {
                logger.error("GuildService.removeMemberFromGuild() - User '${userId.toHexString()}' is the owner of guild '${guildId.toHexString()}'")
                throw GuildRemoveOwnerException()
            }
            if (!guild.hasMember(userId)) {
                logger.error("GuildService.removeMemberFromGuild() - User '${userId.toHexString()}' is not a member of guild '${guildId.toHexString()}'")
                throw GuildMemberNotFoundException(userId)
            }

            logger.info("Removing user $userId from guild ${guild.id}")
            guild.removeMember(userId)
            return saveGuild(guild)
        }
    }

    /**
     * Adds a member to a role. Returns the new guild if successful.
     * If the guild isn't found or the member or role isn't found, this method will throw an exception.
     *
     * @throws GuildNotFoundException If the guild isn't found.
     * @throws GuildMemberNotFoundException If the member isn't found.
     * @throws GuildRoleNotFoundException If the role isn't found.
     *
     * @see GuildNotFoundException
     * @see GuildMemberNotFoundException
     * @see GuildRoleNotFoundException
     */
    @Throws(GuildNotFoundException::class, GuildMemberNotFoundException::class, GuildRoleNotFoundException::class)
    @CachePut(key = "#guildId", unless = "#result == null")
    fun addRolesToMember(guildId: ObjectId, userId: ObjectId, roleIds: List<ObjectId>): Guild {
        findGuildById(guildId).let { guild ->
            if (guild == null) {
                logger.error("GuildService.addMemberToGuild() - Guild '${guildId.toHexString()}' not found")
                throw GuildNotFoundException(guildId)
            }
            if (!guild.hasMember(userId)) {
                logger.error("GuildService.addMemberToGuild() - User '${userId.toHexString()}' not found in guild '${guildId.toHexString()}'")
                throw GuildMemberNotFoundException(userId)
            }
            if (!guild.hasRoles(roleIds)) {
                logger.error("GuildService.addMemberToGuild() - Role '${roleIds.joinToString(", ")}' not found in guild '${guildId.toHexString()}'")
                val missingRoles = roleIds.filter { !guild.roles.any { role -> role.id == it } }
                throw GuildRoleNotFoundException(guildId, missingRoles)
            }

            logger.info("Adding roles to user $userId in guild ${guild.id}")
            val member = guild.getMemberById(userId)!!
            roleIds.forEach { roleId -> member.addRole(roleId) }
            return saveGuild(guild)
        }
    }

    /**
     * Removes roles from a guild member. Returns the new guild if successful.
     * If the guild or member isn't found, this method will throw an exception.
     *
     * @throws GuildNotFoundException If the guild isn't found.
     * @throws GuildMemberNotFoundException If the member isn't found.
     *
     * @param guildId The ID of the guild to modify.
     * @param userId The ID of the user to remove the role from.
     * @param roleIds The IDs of the roles to remove.
     */
    @Throws(GuildNotFoundException::class, GuildMemberNotFoundException::class)
    @CachePut(key = "#guildId", unless = "#result == null")
    fun removeRolesFromMember(guildId: ObjectId, userId: ObjectId, roleIds: List<ObjectId>): Guild {
        findGuildById(guildId).let { guild ->
            if (guild == null) {
                logger.error("GuildService.removeRoleFromMember() - Guild '${guildId.toHexString()}' not found")
                throw GuildNotFoundException(guildId)
            }
            if (!guild.hasMember(userId)) {
                logger.error("GuildService.removeRoleFromMember() - Member '${userId.toHexString()}' not found")
                throw GuildMemberNotFoundException(userId)
            }

            logger.info("Removing ${roleIds.size} roles from user $userId in guild ${guild.id}")
            val member = guild.getMemberById(userId)!!
            roleIds.forEach { roleId -> member.removeRole(roleId) }
            return saveGuild(guild)
        }
    }

    /**
     * Adds a permission to a guild member, returning the new guild if successful.
     * If the guild or member isn't found or the permission isn't valid, this method will throw an exception.
     *
     * @throws GuildNotFoundException If the guild isn't found.
     * @throws GuildMemberNotFoundException If the member isn't found.
     * @throws GuildInvalidPermissionException If the permission isn't valid.
     *
     * @param guildId The ID of the guild to modify.
     * @param userId The ID of the user to add the permission to.
     * @param permissions The permissions to add.
     */
    @Throws(
        GuildNotFoundException::class,
        GuildMemberNotFoundException::class,
        GuildInvalidPermissionException::class
    )
    @CachePut(key = "#guildId", unless = "#result == null")
    fun addPermissionsToMember(guildId: ObjectId, userId: ObjectId, permissions: List<String>): Guild {
        findGuildById(guildId).let { guild ->
            if (guild == null) {
                logger.error("GuildService.addPermissionsToUser() - Guild '${guildId.toHexString()}' not found")
                throw GuildNotFoundException(guildId)
            }
            if (!guild.hasMember(userId)) {
                logger.error("GuildService.addPermissionsToUser() - Member '${userId.toHexString()}' not found")
                throw GuildMemberNotFoundException(userId)
            }
            if (!UserGuildPermission.areValidPermissions(permissions)) {
                logger.error("GuildService.addPermissionsToUser() - One or more permissions are invalid")
                throw GuildInvalidPermissionException(permissions.toList())
            }

            logger.info("Adding ${permissions.size} permissions to user $userId in guild ${guild.id}")
            val member = guild.getMemberById(userId)!!
            permissions.forEach { permission -> member.addPermission(permission) }
            return saveGuild(guild)
        }
    }

    /**
     * Removes a permission from a guild member, returning the new guild if successful.
     * If the guild or member isn't found, this method will throw an exception. Will not check if the permission
     * being removed actually exists on the user.
     *
     * @throws GuildNotFoundException If the guild isn't found.
     * @throws GuildMemberNotFoundException If the member isn't found.
     *
     * @param guildId The ID of the guild to modify.
     * @param userId The ID of the user to remove the permission from.
     */
    @Throws(GuildNotFoundException::class, GuildMemberNotFoundException::class)
    @CachePut(key = "#guildId", unless = "#result == null")
    fun removePermissionsFromMember(guildId: ObjectId, userId: ObjectId, permissions: List<String>): Guild {
        findGuildById(guildId).let { guild ->
            if (guild == null) {
                logger.error("GuildService.removePermissionsFromUser() - Guild '${guildId.toHexString()}' not found")
                throw GuildNotFoundException(guildId)
            }
            if (!guild.hasMember(userId)) {
                logger.error("GuildService.removePermissionsFromUser() - Member '${userId.toHexString()}' not found")
                throw GuildMemberNotFoundException(userId)
            }

            logger.info("Removing ${permissions.size} permissions from user $userId in guild ${guild.id}")
            val member = guild.getMemberById(userId)!!
            member.removePermissions(permissions)
            return saveGuild(guild)
        }
    }

    /**
     * Adds a role to a guild, returning the new guild if successful.
     * If the guild isn't found or the role isn't valid, this method will throw an exception.
     *
     * @throws GuildNotFoundException If the guild isn't found.
     * @throws GuildInvalidPermissionException If the role isn't valid.
     *
     * @param guildId The ID of the guild to modify.
     * @param role The role to add.
     */
    @Throws(GuildNotFoundException::class, GuildInvalidPermissionException::class)
    @CachePut(key = "#guildId", unless = "#result == null")
    fun addRoleToGuild(guildId: ObjectId, role: GuildRole): Guild {
        findGuildById(guildId).let { guild ->
            if (guild == null) {
                logger.error("GuildService.addRoleToGuild() - Guild '${guildId.toHexString()}' not found")
                throw GuildNotFoundException(guildId)
            }
            if (!UserGuildPermission.areValidPermissions(role.permissions.toList())) {
                logger.error("GuildService.addRoleToGuild() - One or more permissions are invalid")
                throw GuildInvalidPermissionException(role.permissions.toList())
            }

            logger.info("Adding role '${role.name}' (${role.id}) to guild ${guild.id}")
            guild.addRole(role)
            return saveGuild(guild)
        }
    }

    /**
     * Removes a role from a guild, returning the new guild if successful.
     * If the guild or role isn't found, this method will throw an exception.
     * Will also throw an exception if the role is the default role.
     * @see [GuildRole.defaultRoleId]
     *
     * @throws GuildNotFoundException If the guild isn't found.
     * @throws GuildRoleNotFoundException If the role isn't found.
     *
     * @param guildId The ID of the guild to modify.
     * @param roleId The ID of the role to remove.
     */
    @Throws(GuildNotFoundException::class, GuildRoleNotFoundException::class)
    @CachePut(key = "#guildId", unless = "#result == null")
    fun removeRoleFromGuild(guildId: ObjectId, roleId: ObjectId): Guild {
        findGuildById(guildId).let { guild ->
            if (guild == null) {
                logger.error("GuildService.removeRoleFromGuild() - Guild '${guildId.toHexString()}' not found")
                throw GuildNotFoundException(guildId)
            }
            if (!guild.hasRole(roleId)) {
                logger.error("GuildService.removeRoleFromGuild() - Role '${roleId.toHexString()}' not found")
                throw GuildRoleNotFoundException(guildId, roleId)
            }

            // Remove role from all members who have it
            guild.members.forEach { member -> member.removeRole(roleId) }

            logger.info("Removing role '${roleId.toHexString()}' from guild ${guild.id}")
            guild.removeRole(roleId)
            return saveGuild(guild)
        }
    }

    /**
     * Adds a permission to a guild role, returning the updated guild if successful.
     * If the guild or role isn't found, or any permission being added is invalid,
     * this method will throw an exception.
     *
     * @throws GuildNotFoundException If the guild isn't found.
     * @throws GuildRoleNotFoundException If the role isn't found.
     * @throws GuildInvalidPermissionException If one or more permissions are invalid.
     *
     * @param guildId The ID of the guild to modify.
     * @param roleId The ID of the role to add the permission to.
     * @param permissions The permissions to add.
     */
    @Throws(GuildNotFoundException::class, GuildRoleNotFoundException::class, GuildInvalidPermissionException::class)
    @CachePut(key = "#guildId", unless = "#result == null")
    fun addPermissionsToRole(guildId: ObjectId, roleId: ObjectId, permissions: List<String>): Guild {
        findGuildById(guildId).let { guild ->
            if (guild == null) {
                logger.error("GuildService.addPermissionToRole() - Guild '${guildId.toHexString()}' not found")
                throw GuildNotFoundException(guildId)
            }
            if (!guild.hasRole(roleId)) {
                logger.error("GuildService.addPermissionToRole() - Role '${roleId.toHexString()}' not found")
                throw GuildRoleNotFoundException(guildId, roleId)
            }
            if (!UserGuildPermission.areValidPermissions(permissions)) {
                logger.error("GuildService.addPermissionToRole() - One or more permissions are invalid")
                throw GuildInvalidPermissionException(permissions.toList())
            }

            guild.roles.find { it.id == roleId }?.addPermissions(permissions)
            return saveGuild(guild)
        }
    }

    /**
     * Removes permissions from a guild role, returning the updated guild if successful.
     * If the guild or role isn't found, this method will throw an exception.
     *
     * Permissions being removed are not checked for validity.
     *
     * @throws GuildNotFoundException If the guild isn't found.
     * @throws GuildRoleNotFoundException If the role isn't found.
     *
     * @param guildId The ID of the guild to modify.
     * @param roleId The ID of the role to remove the permissions from.
     * @param permissions The permissions to remove.
     */
    @Throws(GuildNotFoundException::class, GuildRoleNotFoundException::class)
    @CachePut(key = "#guildId", unless = "#result == null")
    fun removePermissionsFromRole(guildId: ObjectId, roleId: ObjectId, permissions: List<String>): Guild {
        findGuildById(guildId).let { guild ->
            if (guild == null) {
                logger.error("GuildService.removePermissionsFromRole() - Guild '${guildId.toHexString()}' not found")
                throw GuildNotFoundException(guildId)
            }
            if (!guild.hasRole(roleId)) {
                logger.error("GuildService.removePermissionsFromRole() - Role '${roleId.toHexString()}' not found")
                throw GuildRoleNotFoundException(guildId, roleId)
            }

            guild.roles.find { it.id == roleId }?.removePermissions(permissions)
            return saveGuild(guild)
        }
    }

    /**
     * Creates a new guild with the given owner. Returns the new guild.
     * If the user is already in a guild, this method will throw an exception.
     *
     * @throws UserAlreadyInGuildException If the user is already in a guild.
     *
     * @param owner The owner of the guild. [UserDto] is used to initialize the first guild member (owner).
     */
    @Throws(UserAlreadyInGuildException::class)
    @CachePut(key = "#result.id", unless = "#result == null")
    fun createGuild(owner: UserDto): Guild {
        val userId = ObjectIdHelper.getId(owner.id)
        if (findGuildUserIsIn(userId) != null) {
            logger.error("GuildService.createGuild() - User '${owner.id}' is already in a guild")
            throw UserAlreadyInGuildException(owner.id)
        }

        val guild = Guild(userId)
        val ownerMember = GuildMember(userId, owner.username)
        guild.addMember(ownerMember)

        return saveGuild(guild)
    }

    /**
     * Saves a guild to the database. If the guild already exists, it will be updated.
     * Returns the saved guild.
     *
     * @param guild The guild to save.
     */
    @CachePut(key = "#guild.id")
    fun saveGuild(guild: Guild): Guild {
        return guildRepository.save(guild)
    }

    /**
     * Deletes a guild from the database.
     */
    @CacheEvict(key = "#guildId")
    fun deleteGuild(guildId: ObjectId): Boolean {
        guildRepository.existsById(guildId.toHexString()).let {
            if (it) {
                guildRepository.deleteById(guildId.toHexString())
                return true
            }
            return false
        }
    }

}
