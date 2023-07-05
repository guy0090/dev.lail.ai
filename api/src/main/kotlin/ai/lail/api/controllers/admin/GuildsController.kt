package ai.lail.api.controllers.admin

import ai.lail.api.annotations.ControllerPermission
import ai.lail.api.annotations.RoutePermission
import ai.lail.api.controllers.AdminController
import ai.lail.api.data.guilds.Guild
import ai.lail.api.data.guilds.GuildRole
import ai.lail.api.data.guilds.requests.*
import ai.lail.api.data.users.AuthUser
import ai.lail.api.dto.responses.users.UserDto
import ai.lail.api.exceptions.guilds.GuildInvalidPermissionException
import ai.lail.api.exceptions.guilds.GuildNotFoundException
import ai.lail.api.exceptions.guilds.GuildRoleNotFoundException
import ai.lail.api.exceptions.settings.users.UserSettingsNotFoundException
import ai.lail.api.exceptions.users.UserNotFoundException
import ai.lail.api.permissions.nodes.GuildPermission
import ai.lail.api.security.SecurityConstants.AUTH_USER
import ai.lail.api.services.GuildService
import ai.lail.api.services.UserService
import ai.lail.api.services.UserSettingsService
import ai.lail.api.util.ObjectIdHelper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*

@RestController
@ControllerPermission(GuildPermission.MANAGE)
class GuildsController(
    val guildService: GuildService,
    val userService: UserService,
    val userSettingsService: UserSettingsService
) :
    AdminController() {
    val logger: Logger = LoggerFactory.getLogger(GuildsController::class.java)

    @RoutePermission(GuildPermission.CREATE)
    @PostMapping("$ROUTE/create")
    fun createGuild(@RequestAttribute(AUTH_USER) authUser: AuthUser?): Guild {
        val user = authUser?.user ?: throw UserNotFoundException()
        val creatorSettings = userSettingsService.find(user.id) ?: throw UserSettingsNotFoundException(user.id)
        val userDto = UserDto(user, creatorSettings)

        return guildService.createGuild(userDto)
    }

    @RoutePermission(GuildPermission.MANAGE_VIEW)
    @GetMapping("$ROUTE/get/{guildId}")
    fun getGuild(@PathVariable guildId: String): Guild? {
        val targetGuild = ObjectIdHelper.getId(guildId)
        return guildService.findGuildById(targetGuild)
    }

    @RoutePermission(GuildPermission.MANAGE_DELETE)
    @DeleteMapping("$ROUTE/delete/{guildId}")
    fun deleteGuild(@PathVariable guildId: String): Boolean {
        val targetGuild = ObjectIdHelper.getId(guildId)
        return guildService.deleteGuild(targetGuild)
    }

    /* // Guild modification routes // */

    /* //         Members           // */

    @RoutePermission(GuildPermission.MANAGE_MEMBERS_ADD)
    @PostMapping("$ROUTE/{guildId}/members/add/{userId}")
    fun addMember(@PathVariable guildId: String, @PathVariable userId: String): Guild? {
        val targetGuildId = ObjectIdHelper.getId(guildId)
        val targetUserId = ObjectIdHelper.getId(userId)

        val user = userService.find(targetUserId) ?: throw UserNotFoundException(targetUserId)
        val creatorSettings = userSettingsService.find(user.id) ?: throw UserSettingsNotFoundException(user.id)
        val userDto = UserDto(user, creatorSettings)

        guildService.findGuildById(targetGuildId).let { guild ->
            if (guild == null) throw GuildNotFoundException(guildId)
            return guildService.addMemberToGuild(targetGuildId, userDto)
        }
    }

    @RoutePermission(GuildPermission.MANAGE_MEMBERS_REMOVE)
    @DeleteMapping("$ROUTE/{guildId}/members/remove/{userId}")
    fun removeMember(@PathVariable guildId: String, @PathVariable userId: String): Guild? {
        val targetGuild = ObjectIdHelper.getId(guildId)
        val targetUser = ObjectIdHelper.getId(userId)
        userService.find(targetUser) ?: throw UserNotFoundException(targetUser)

        guildService.findGuildById(targetGuild).let { guild ->
            if (guild == null) throw GuildNotFoundException(guildId)
            return guildService.removeMemberFromGuild(targetGuild, targetUser)
        }
    }

    /* //    Member Permissions     // */

    @RoutePermission(GuildPermission.MANAGE_MEMBERS_PERMISSIONS_ADD)
    @PatchMapping("$ROUTE/{guildId}/members/{userId}/permissions/add")
    fun addUserPermission(
        @PathVariable guildId: String,
        @PathVariable userId: String,
        @RequestBody request: PermissionBody
    ): Guild? {
        val targetGuild = ObjectIdHelper.getId(guildId)
        val targetUser = ObjectIdHelper.getId(userId)
        guildService.findGuildById(targetGuild).let { guild ->
            if (guild == null) throw GuildNotFoundException(guildId)
            return guildService.addPermissionsToMember(targetGuild, targetUser, request.permissions)
        }
    }

    @RoutePermission(GuildPermission.MANAGE_MEMBERS_PERMISSIONS_REMOVE)
    @PatchMapping("$ROUTE/{guildId}/members/{userId}/permissions/remove")
    fun removeUserPermission(
        @PathVariable guildId: String,
        @PathVariable userId: String,
        @RequestBody request: PermissionBody
    ): Guild? {
        val targetGuild = ObjectIdHelper.getId(guildId)
        val targetUser = ObjectIdHelper.getId(userId)
        guildService.findGuildById(targetGuild).let { guild ->
            if (guild == null) throw GuildNotFoundException(guildId)
            return guildService.removePermissionsFromMember(
                targetGuild,
                targetUser,
                request.permissions
            )
        }
    }

    /* //       Member Roles        // */

    @RoutePermission(GuildPermission.MANAGE_MEMBERS_ROLES_ADD)
    @PatchMapping("$ROUTE/{guildId}/members/{userId}/roles/add")
    fun addRolesToUser(
        @PathVariable guildId: String,
        @PathVariable userId: String,
        @RequestBody request: MemberRoleBody
    ): Guild {
        val targetGuild = ObjectIdHelper.getId(guildId)
        val targetUser = ObjectIdHelper.getId(userId)
        val targetRoles = request.roles.map { ObjectIdHelper.getId(it) }
        return guildService.addRolesToMember(targetGuild, targetUser, targetRoles)
    }

    @RoutePermission(GuildPermission.MANAGE_MEMBERS_ROLES_REMOVE)
    @PatchMapping("$ROUTE/{guildId}/members/{userId}/roles/remove")
    fun removedRolesFromUser(
        @PathVariable guildId: String,
        @PathVariable userId: String,
        @RequestBody request: MemberRoleBody
    ): Guild {
        val targetGuild = ObjectIdHelper.getId(guildId)
        val targetUser = ObjectIdHelper.getId(userId)
        val targetRoles = request.roles.map { ObjectIdHelper.getId(it) }
        return guildService.removeRolesFromMember(targetGuild, targetUser, targetRoles)
    }

    /* //           Roles           // */

    @RoutePermission(GuildPermission.MANAGE_ROLES_ADD)
    @PostMapping("$ROUTE/{guildId}/roles/add")
    fun createRole(@PathVariable guildId: String, @RequestBody request: RoleCreateBody): Guild? {
        return try {
            val targetGuild = ObjectIdHelper.getId(guildId)
            val guild = guildService.findGuildById(targetGuild) ?: throw GuildNotFoundException(guildId)
            val permissions = request.permissions
            checkValidPermissions(request.permissions)

            val role = GuildRole(
                name = request.name,
                color = request.color,
                permissions = permissions.toMutableSet()
            )
            return guildService.addRoleToGuild(guild.id, role)
        } catch (e: Exception) {
            logger.warn("Invalid request: ${e.message}")
            null
        }
    }

    @RoutePermission(GuildPermission.MANAGE_ROLES_REMOVE)
    @DeleteMapping("$ROUTE/{guildId}/roles/delete/{roleId}")
    fun deleteRole(@PathVariable guildId: String, @PathVariable roleId: String): Guild? {
        val targetGuild = ObjectIdHelper.getId(guildId)
        val targetRole = ObjectIdHelper.getId(roleId)
        return guildService.removeRoleFromGuild(targetGuild, targetRole)
    }

    /* //      Role Permissions     // */

    @RoutePermission(GuildPermission.MANAGE_ROLES_PERMISSIONS_ADD)
    @PatchMapping("$ROUTE/{guildId}/roles/{roleId}/permissions/add")
    fun addRolePermissions(
        @PathVariable guildId: String,
        @PathVariable roleId: String,
        @RequestBody request: PermissionBody
    ): Guild {
        val targetGuild = ObjectIdHelper.getId(guildId)
        val targetRole = ObjectIdHelper.getId(roleId)
        val permissions = request.permissions
        return guildService.addPermissionsToRole(targetGuild, targetRole, permissions)
    }

    @RoutePermission(GuildPermission.MANAGE_ROLES_PERMISSIONS_REMOVE)
    @PatchMapping("$ROUTE/{guildId}/roles/{roleId}/permissions/remove")
    fun removeRolePermissions(
        @PathVariable guildId: String,
        @PathVariable roleId: String,
        @RequestBody request: PermissionBody
    ): Guild? {
        val targetGuild = ObjectIdHelper.getId(guildId)
        val targetRole = ObjectIdHelper.getId(roleId)
        val permissions = request.permissions
        checkValidPermissions(permissions)

        val guild = guildService.findGuildById(targetGuild) ?: throw GuildNotFoundException(guildId)
        guild.roles.find { it.id == targetRole }.let { role ->
            if (role == null) throw GuildRoleNotFoundException(targetGuild, targetRole)
            return guildService.removePermissionsFromRole(guild.id, role.id, permissions)
        }
    }

    private fun checkValidPermissions(permissions: List<String>): Boolean {
        if (!GuildPermission.areValidPermissions(permissions)) {
            val invalid = permissions.filter { !GuildPermission.isValidPermission(it) }
            throw GuildInvalidPermissionException(invalid)
        }
        return true
    }

    companion object {
        const val ROUTE = "/guilds"
    }
}