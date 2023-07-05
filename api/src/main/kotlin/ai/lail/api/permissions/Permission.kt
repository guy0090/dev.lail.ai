package ai.lail.api.permissions

import ai.lail.api.permissions.nodes.*
import ai.lail.api.util.Reflect
import kotlin.reflect.full.memberProperties

object Permission {
    const val ROOT = "*"

    /**
     * Permissions related to administrative tasks.
     */
    val ADMIN = AdminPermission

    /**
     * Permissions related to User actions.
     */
    val USER = UserPermission

    /**
     * Permissions related to managing roles.
     */
    val ROLE = RolePermission

    /**
     * Permissions related to managing encounters.
     */
    val ENCOUNTER = EncounterPermission

    /**
     * Permissions related to managing Guilds.
     */
    val GUILD = GuildPermission

    /**
     * Permissions available within the scope of a Guild.
     */
    val USER_GUILD = UserGuildPermission

    /**
     * Permissions related sending and receiving WebSocket messages.
     */
    val WEB_SOCKET = WebSocketPermission

    /**
     * Returns all permissions.
     * All permissions are found within `ai.lail.api.permissions.nodes` and are
     * collected via. reflection and returned as a set. Might be slow on initial call.
     *
     * @return A set of all permissions.
     */
    private fun getAllPermissions(): Set<String> {
        val nestedPermissionObjects = this::class.memberProperties.filter {
            it.returnType.toString().startsWith("ai.lail.api.permissions.nodes")
        }

        val permissions = mutableSetOf("*")
        nestedPermissionObjects.forEach { clazz ->
            val instance = clazz.call(this)!!::class
            val classPermissions = Reflect.getAllConstValues(instance)
            permissions.addAll(classPermissions)
        }
        return permissions
    }

    /**
     * Checks if a permission is valid.
     * @param permission The permission to check.
     */
    fun isValidPermission(permission: String): Boolean {
        if (permission == ROOT) return false // Root is not a valid permission.
        return getAllPermissions().contains(permission)
    }

    /**
     * Checks if a list of permissions are valid.
     * @param permissions The permissions to check.
     */
    fun areValidPermissions(permissions: Collection<String>): Boolean {
        return getAllPermissions().containsAll(permissions)
    }
}